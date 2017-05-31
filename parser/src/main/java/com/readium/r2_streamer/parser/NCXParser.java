package com.readium.r2_streamer.parser;

import com.readium.r2_streamer.model.container.Container;
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

    public static ToC parseNCXFile(String ncxFile, String packageName, Container container) throws EpubParserException {
        String ncxData = container.rawData(ncxFile);
        if (ncxData == null) {
            return null; // File is missing
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
        NodeList navPointNodes = navMapElement.getChildNodes();
        if (navPointNodes != null) {
            for (int i = 0; i < navPointNodes.getLength(); i++) {
                Node node = navPointNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element navPointElement = (Element) navPointNodes.item(i);
                    tableOfContents.tocLinks.add(parseNavPointElement(navPointElement, packageName));
                }
            }
            tableOfContents.tocLinks.size();
            System.out.println(TAG + " navPointNodes " + tableOfContents);
        }
        return tableOfContents;
    }

    //@Nullable
    private static TOCLink parseNavPointElement(Element element, String packageName) {
        TOCLink tocLink = new TOCLink();
        tocLink.setId(element.getAttribute("id"));
        tocLink.setPlayOrder(element.getAttribute("playOrder"));

        List<TOCLink> navPointList = new ArrayList<>();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeName().equals("navLabel")) {
                NodeList labelNodes = element.getElementsByTagName("navLabel");
                tocLink.setSectionTitle(parseTOCLabel(labelNodes));
            } else if (childNode.getNodeName().equals("content")) {
                NodeList contentNodes = element.getElementsByTagName("content");
                tocLink.setHref(packageName + parseTOCResourceLink(contentNodes));
            } else if (childNode.getNodeName().equals("navPoint")) {
                Element navPointElement = (Element) childNode;
                navPointList.add(parseNavPointElement(navPointElement, packageName));
            }
        }

        if (navPointList.size() > 0) {
            tocLink.setTocLinks(navPointList);
        }
        return tocLink;
    }

    private static String parseTOCLabel(NodeList nodeList) {
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element labelElement = (Element) nodeList.item(i);
                NodeList textNodeList = labelElement.getElementsByTagName("text");
                if (textNodeList != null) {
                    for (int j = 0; j < textNodeList.getLength(); j++) {
                        Element textElement = (Element) textNodeList.item(j);
                        return textElement.getTextContent();
                    }
                }
            }
        }
        return null;
    }

    private static String parseTOCResourceLink(NodeList nodeList) {
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element srcElement = (Element) nodeList.item(i);
                return srcElement.getAttribute("src");
            }
        }
        return null;
    }
}
