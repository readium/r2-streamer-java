package org.readium.r2_streamer.parser;

import org.readium.r2_streamer.model.container.Container;
import org.readium.r2_streamer.model.publication.EpubPublication;
import org.readium.r2_streamer.model.publication.SMIL.MediaOverlayNode;
import org.readium.r2_streamer.model.publication.SMIL.SMILParser;
import org.readium.r2_streamer.model.publication.link.Link;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by gautam chibde on 31/5/17.
 */

public class MediaOverlayParser {

    /**
     * Looks for the link with type: application/smil+xml and parsed the
     * data as media-overlay
     * also adds link for media-overlay for specific file
     *
     * @param publication The `Publication` object resulting from the parsing.
     * @param container   contains implementation for getting raw data from file
     * @throws EpubParserException if file is invalid for not found
     */
    public static void parseMediaOverlay(EpubPublication publication, Container container) throws EpubParserException {
        for (String key : publication.linkMap.keySet()) {
            if (publication.linkMap.get(key).typeLink.equalsIgnoreCase("application/smil+xml")) {
                Link link = publication.linkMap.get(key);
                String smip = container.rawData(link.getHref());
                if (smip == null) return; // maybe file is invalid

                Document document = EpubParser.xmlParser(smip);

                if (document == null)
                    throw new EpubParserException("Error while parsing file " + link.href);

                Element body = (Element) document.getDocumentElement().getElementsByTagNameNS("*", "body").item(0);

                MediaOverlayNode node = new MediaOverlayNode();
                node.role.add("section");

                if (body.hasAttribute("epub:textref"))
                    node.text = body.getAttribute("epub:textref");

                parseParameters(body, node, link.href);
                parseSequences(body, node, publication, link.href);

                // TODO
                // Body attribute epub:textref is optional
                // ref https://www.idpf.org/epub/30/spec/epub30-mediaoverlays.html#sec-smil-body-elem
                // need to handle <seq> parsing in an alternate way

                if (node.text != null) {
                    String baseHref = node.text.split("#")[0];
                    int position = getPosition(publication.spines, baseHref);

                    if (position != -1) {
                        addMediaOverlayToSpine(publication, node, position);
                    }
                } else {
                    for (MediaOverlayNode node1 : node.children) {
                        int position = getPosition(publication.spines, node1.text);
                        if (position != -1) {
                            addMediaOverlayToSpine(publication, node1, position);
                        }
                    }
                }
            }
        }
    }

    /**
     * [RECURSIVE]
     * <p>
     * Parse the <seq> elements at the current XML level. It will recursively
     * parse their children's <par> and <seq>
     *
     * @param body input element with seq tag
     * @param node contains parsed <seq><par></par></seq> elements
     * @param href path of SMIL file
     */
    private static void parseSequences(Element body, MediaOverlayNode node, EpubPublication publication, String href) throws StackOverflowError {
        if (body == null || !body.hasChildNodes()) {
            return;
        }
        for (Node n = body.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                if (e.getTagName().equalsIgnoreCase("seq")) {
                    MediaOverlayNode mediaOverlayNode = new MediaOverlayNode();

                    if (e.hasAttribute("epub:textref"))
                        mediaOverlayNode.text = e.getAttribute("epub:textref");

                    mediaOverlayNode.role.add("section");

                    // child <par> elements in seq
                    parseParameters(e, mediaOverlayNode, href);
                    node.children.add(mediaOverlayNode);
                    // recur to parse child node elements
                    parseSequences(e, mediaOverlayNode, publication, href);

                    if (node.text == null) return;

                    // Not clear about the IRI reference, epub:textref in seq may not have [ "#" ifragment ]
                    // ref:- https://www.idpf.org/epub/30/spec/epub30-mediaoverlays.html#sec-smil-seq-elem
                    // TODO is it req? code ref from https://github.com/readium/r2-streamer-swift/blob/feature/media-overlay/Sources/parser/SMILParser.swift
                    // can be done with contains?

                    String baseHrefParent = node.text;
                    if (node.text.contains("#")) {
                        baseHrefParent = node.text.split("#")[0];
                    }
                    if (mediaOverlayNode.text.contains("#")) {
                        String baseHref = mediaOverlayNode.text.split("#")[0];

                        if (!baseHref.equals(baseHrefParent)) {
                            int position = getPosition(publication.spines, baseHref);

                            if (position != -1)
                                addMediaOverlayToSpine(publication, mediaOverlayNode, position);
                        }
                    }
                }
            }
        }
    }

    /**
     * Parse the <par> elements at the current XML element level.
     *
     * @param body input element with seq tag
     * @param node contains parsed <par></par> elements
     */
    private static void parseParameters(Element body, MediaOverlayNode node, String href) {
        NodeList par = body.getElementsByTagNameNS("*", "par");
        if (par.getLength() == 0) {
            return;
        }
        // For each <par> in the current scope.
        for (Node n = body.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                if (e.getTagName().equalsIgnoreCase("par")) {
                    MediaOverlayNode mediaOverlayNode = new MediaOverlayNode();
                    Element text = (Element) e.getElementsByTagNameNS("*", "text").item(0);
                    Element audio = (Element) e.getElementsByTagNameNS("*", "audio").item(0);

                    if (text != null) mediaOverlayNode.text = text.getAttribute("src");
                    if (audio != null) {
                        mediaOverlayNode.audio = SMILParser.parseAudio(audio, href);
                    }
                    node.children.add(mediaOverlayNode);
                }
            }
        }
    }

    /**
     * Add parsed media-overlay object to corresponding spine item
     *
     * @param publication publication object
     * @param node        parsed media overlay node
     * @param position    position on spine item in publication
     */
    private static void addMediaOverlayToSpine(EpubPublication publication, MediaOverlayNode node, int position) {
        publication.spines.get(position).mediaOverlay.mediaOverlayNodes.add(node);
        publication.spines.get(position).properties.add("media-overlay?resource=" + publication.spines.get(position).href);

        publication.links.add(new Link(
                "port/media-overlay?resource=" + publication.spines.get(position).href, //replace the port with proper url in EpubServer#addLinks
                "media-overlay",
                "application/vnd.readium.mo+json"));
    }

    /**
     * returns position of the spine whose href equals baseHref
     *
     * @param spines   spine list in publication
     * @param baseHref name of the file which corresponding to media-overlay
     * @return returns position of the spine item
     */
    private static int getPosition(List<Link> spines, String baseHref) {
        for (Link link : spines) {
            int offset = link.href.indexOf("/", 0);
            int startIndex = link.href.indexOf("/", offset + 1);
            String path = link.href.substring(startIndex + 1);
            if (baseHref.contains(path)) {
                return spines.indexOf(link);
            }
        }
        return -1;
    }
}
