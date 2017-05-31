package com.readium.r2_streamer.parser;

import com.readium.r2_streamer.model.container.Container;
import com.readium.r2_streamer.model.publication.EpubPublication;
import com.readium.r2_streamer.model.tableofcontents.TOCLink;
import com.readium.r2_streamer.model.tableofcontents.ToC;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gautam chibde on 31/5/17.
 */

public class NCXParser {

    private static final String TAG = NCXParser.class.getSimpleName();

    public static void parseNCXFile(String ncxFile, Container container, EpubPublication publication) throws EpubParserException {
        String ncxData = container.rawData(ncxFile);
        if (ncxData == null) {
            return; // File is missing
        }
        System.out.println(TAG + " ncxData " + ncxData);
        Document document = EpubParser.xmlParser(ncxData);
        if (document == null) {
            throw new EpubParserException("Error while parsing");
        }

        ToC tableOfContents = new ToC();
        Element docTitleElement = (Element) document.getElementsByTagName("docTitle").item(0);
        if (docTitleElement != null) {
            tableOfContents.setDocTitle(docTitleElement.getTextContent());
        }
        Element navMapElement = (Element) document.getElementsByTagName("navMap").item(0);
        // Parse table of contents (toc) from ncx file
        if (navMapElement != null) {
            publication.tableOfContents = nodeArray(navMapElement, "navPoint");
        }

        Element pageList = (Element) document.getElementsByTagName("pageList").item(0);
        // Parse page list if exists from ncx file
        if (pageList != null) {
            publication.pageList = nodeArray(pageList, "pageTarget");
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
    private static List<TOCLink> nodeArray(Element elements, String type) {
        // The "to be returned" node array.
        List<TOCLink> newNodeArray = new ArrayList<>();

        // Find the elements of `type` in the XML element.
        for (Node n = elements.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                if (e.getTagName().equalsIgnoreCase(type)) {
                    newNodeArray.add(node(e, type));
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
    private static TOCLink node(Element element, String type) {
        TOCLink newNode = new TOCLink();

        Element content = (Element) element.getElementsByTagName("content").item(0);
        Element navLabel = (Element) element.getElementsByTagName("navLabel").item(0);
        if (content != null) {
            newNode.href = content.getAttribute("src");
        }
        if (navLabel != null) {
            Element text = (Element) navLabel.getElementsByTagName("text").item(0);
            if (text != null) {
                newNode.bookTitle = text.getTextContent();
            }
        }

        NodeList childerns = element.getElementsByTagName(type);
        for (int i = 0; i < childerns.getLength(); i++) {
            Element e = (Element) childerns.item(i);
            newNode.tocLinks.add(node(e, type));
        }
        return newNode;
    }
}
