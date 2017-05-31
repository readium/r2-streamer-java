package com.readium.r2_streamer.parser;

import com.readium.r2_streamer.model.container.Container;
import com.readium.r2_streamer.model.publication.EpubPublication;
import com.readium.r2_streamer.model.publication.SMIL.MediaOverlayNode;
import com.readium.r2_streamer.model.publication.SMIL.SMILParser;
import com.readium.r2_streamer.model.publication.link.Link;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by gautam chibde on 31/5/17.
 */

public class MediaOverlayParser {
    /**
     * Looks for the link with type: application/smil+xml and parsed the
     * data as media-overlay
     * also adds link for media-overlay for specific file
     *
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

                Element body = (Element) document.getDocumentElement().getElementsByTagName("body").item(0);

                MediaOverlayNode node = new MediaOverlayNode();
                node.role.add("Section");
                if (body.hasAttribute("epub:textref"))
                    node.text = body.getAttribute("epub:textref");
                parseParameters(body, node);
                parseSequences(body, node);
                link.mediaOverlay.text = key;
                link.mediaOverlay.mediaOverlayNodes.add(node);
                publication.linkMap.put(key, link);
                publication.links.add(new Link(
                        "port/media-overlay?resource=" + key, //replace the port with proper url in EpubServer#addLinks
                        "media-overlay",
                        "application/vnd.readium.mo+json"));
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
     * @param node contains parsed <par></par> elements
     */
    private static void parseSequences(Element body, MediaOverlayNode node) throws StackOverflowError {
        for (Node n = body.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                if (e.getTagName().equalsIgnoreCase("seq")) {
                    MediaOverlayNode mediaOverlayNode = new MediaOverlayNode();

                    mediaOverlayNode.role.add("Section");

                    if (e.hasAttribute("epub:textref"))
                        mediaOverlayNode.text = e.getAttribute("epub:textref");
                    parseParameters(e, mediaOverlayNode);

                    node.children.add(mediaOverlayNode); // child <par> elements in seq

                    // recur to parse child node elements
                    if (e.getElementsByTagName("seq").getLength() != 0) {
                        parseSequences(e, mediaOverlayNode);
                    }
                }
            }
        }
    }

    private static void parseParameters(Element body, MediaOverlayNode node) {
        NodeList par = body.getElementsByTagName("par");
        if (par.getLength() == 0) {
            return;
        }
        // For each <par> in the current scope.
        for (Node n = body.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                if (e.getTagName().equalsIgnoreCase("par")) {
                    MediaOverlayNode mediaOverlayNode = new MediaOverlayNode();
                    Element text = (Element) e.getElementsByTagName("text").item(0);
                    Element audio = (Element) e.getElementsByTagName("audio").item(0);

                    if (text != null) mediaOverlayNode.text = text.getAttribute("src");
                    if (audio != null) {
                        mediaOverlayNode.audio = SMILParser.parseAudio(audio);
                    }
                    node.children.add(mediaOverlayNode);
                }
            }
        }
    }
}
