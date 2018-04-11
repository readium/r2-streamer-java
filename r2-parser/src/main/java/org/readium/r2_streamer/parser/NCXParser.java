package org.readium.r2_streamer.parser;

import org.readium.r2_streamer.model.container.Container;
import org.readium.r2_streamer.model.publication.EpubPublication;
import org.readium.r2_streamer.model.tableofcontents.TOCLink;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gautam chibde on 31/5/17.
 */

public class NCXParser {

    private static final String TAG = NCXParser.class.getSimpleName();

    private static final String UTF8_BOM = "\uFEFF";

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public static void parseNCXFile(String ncxFile, Container container, EpubPublication publication, String rootPath) throws EpubParserException {
        String ncxData = container.rawData(ncxFile);
        if (ncxData == null) {
            return; // File is missing
        }

        ncxData = removeUTF8BOM(ncxData);
        Document document = EpubParser.xmlParser(ncxData);
        if (document == null) {
            throw new EpubParserException("Error while parsing");
        }

        Element navMapElement = (Element) document.getElementsByTagNameNS("*", "navMap").item(0);
        // Parse table of contents (toc) from ncx file
        if (navMapElement != null) {
            publication.tableOfContents = nodeArray(navMapElement, "navPoint", rootPath);
        }

        Element pageList = (Element) document.getElementsByTagNameNS("*", "pageList").item(0);
        // Parse page list if exists from ncx file
        if (pageList != null) {
            publication.pageList = nodeArray(pageList, "pageTarget", rootPath);
        }
    }

    /**
     * Generate an array of {@link TOCLink} elements representation of the XML
     * structure in the ncx file. Each of them possibly having children.
     *
     * @param elements NCX DOM element object
     * @param type     The sub elements names (e.g. 'navPoint' for 'navMap',
     *                 'pageTarget' for 'pageList'.
     * @return The Object representation of the data contained in the given NCX XML element.
     */
    private static List<TOCLink> nodeArray(Element elements, String type, String rootPath) {
        // The "to be returned" node array.
        List<TOCLink> newNodeArray = new ArrayList<>();

        // Find the elements of `type` in the XML element.
        for (Node n = elements.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                if (e.getTagName().equalsIgnoreCase(type)) {
                    newNodeArray.add(node(e, type, rootPath));
                }
            }
        }
        return newNodeArray;
    }

    /**
     * [RECURSIVE]
     * Create a node link from the specified type element.
     * recur if there are child elements
     *
     * @param element the DOM NCX file elemet
     * @param type    The sub elements names (e.g. 'navPoint' for 'navMap',
     *                'pageTarget' for 'pageList'.
     * @return The generated node {@link TOCLink}.
     */
    private static TOCLink node(Element element, String type, String rootPath) {
        TOCLink newNode = new TOCLink();

        Element content = (Element) element.getElementsByTagNameNS("*", "content").item(0);
        Element navLabel = (Element) element.getElementsByTagNameNS("*", "navLabel").item(0);
        if (content != null) {
            newNode.href = rootPath + content.getAttribute("src");
        }
        if (navLabel != null) {
            Element text = (Element) navLabel.getElementsByTagNameNS("*", "text").item(0);
            if (text != null) {
                newNode.bookTitle = text.getTextContent();
            }
        }

        for (Node n = element.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                if (e.getTagName().equalsIgnoreCase(type)) {
                    newNode.tocLinks.add(node(e, type, rootPath));
                }
            }
        }
        return newNode;
    }
}
