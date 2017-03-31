package com.readium.r2_streamer.parser;

import android.support.annotation.Nullable;
import android.util.Log;

import com.readium.r2_streamer.model.container.Container;
import com.readium.r2_streamer.model.publication.EpubPublication;
import com.readium.r2_streamer.model.publication.contributor.Contributor;
import com.readium.r2_streamer.model.publication.link.Link;
import com.readium.r2_streamer.model.publication.metadata.MetaData;
import com.readium.r2_streamer.model.publication.rendition.RenditionFlow;
import com.readium.r2_streamer.model.publication.rendition.RenditionLayout;
import com.readium.r2_streamer.model.publication.rendition.RenditionOrientation;
import com.readium.r2_streamer.model.publication.rendition.RenditionSpread;
import com.readium.r2_streamer.model.publication.subject.Subject;
import com.readium.r2_streamer.model.tableofcontents.ToC;
import com.readium.r2_streamer.model.tableofcontents.TOCLink;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Shrikant Badwaik on 27-Jan-17.
 */

public class EpubParser {
    private final String TAG = "EpubParser";

    private Container container;        //can be either EpubContainer or DirectoryContainer
    private EpubPublication publication;
    //private String epubVersion;

    public EpubParser(Container container) {
        this.container = container;
    }

    public EpubPublication parseEpubFile() {
        String rootFile;
        try {
            if (isMimeTypeValid()) {
                rootFile = parseContainer();
                this.publication = parseOpfFile(rootFile);
                return publication;
            }
        } catch (EpubParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isMimeTypeValid() throws EpubParserException {
        String mimeTypeData = container.rawData("mimetype");

        if (mimeTypeData.equals("application/epub+zip")) {
            Log.d(TAG, "MIME type: " + mimeTypeData);
            return true;
        } else {
            Log.e(TAG, "Invalid MIME type: " + mimeTypeData);
            throw new EpubParserException("Invalid MIME type");
        }
    }

    private String parseContainer() throws EpubParserException {
        String containerPath = "META-INF/container.xml";
        String containerData = container.rawData(containerPath);

        if (containerData == null) {
            Log.e(TAG, "File is missing: " + containerPath);
            throw new EpubParserException("File is missing");
        }

        String opfFile = containerXmlParser(containerData);
        if (opfFile == null) {
            throw new EpubParserException("Error while parsing");
        }
        return opfFile;
    }

    @Nullable
    private String containerXmlParser(String containerData) throws EpubParserException {           //parsing container.xml
        try {
            String xml = containerData.replaceAll("[^\\x20-\\x7e]", "").trim();         //in case encoding problem

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));
            document.getDocumentElement().normalize();
            if (document == null) {
                throw new EpubParserException("Error while parsing container.xml");
            }

            Element rootElement = (Element) ((Element) document.getDocumentElement().getElementsByTagName("rootfiles").item(0)).getElementsByTagName("rootfile").item(0);
            if (rootElement != null) {
                String opfFile = rootElement.getAttribute("full-path");
                if (opfFile == null) {
                    throw new EpubParserException("Missing root file element in container.xml");
                }

                Log.d(TAG, "Root file: " + opfFile);
                return opfFile;                    //returns opf file
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private EpubPublication parseOpfFile(String rootFile) throws EpubParserException {
        String opfData = container.rawData(rootFile);
        if (opfData == null) {
            Log.e(TAG, "File is missing: " + rootFile);
            throw new EpubParserException("File is missing");
        }

        Document document = xmlParser(opfData);
        if (document == null) {
            throw new EpubParserException("Error while parsing");
        }

        this.publication = new EpubPublication();
        publication.internalData.put("type", "epub");
        publication.internalData.put("rootfile", rootFile);

        MetaData metaData = new MetaData();

        //title
        metaData.title = parseMainTitle(document);
        Log.d(TAG, "Title: " + metaData.getTitle());

        //identifier
        metaData.identifier = parseUniqueIdentifier(document);
        Log.d(TAG, "Identifier: " + metaData.getIdentifier());

        //description
        Element descriptionElement = (Element) ((Element) document.getDocumentElement().getElementsByTagName("metadata").item(0)).getElementsByTagName("dc:description").item(0);
        if (descriptionElement != null) {
            metaData.description = descriptionElement.getTextContent();
        }
        Log.d(TAG, "Description: " + metaData.getDescription());

        //modified date
        Element dateElement = (Element) ((Element) document.getDocumentElement().getElementsByTagName("metadata").item(0)).getElementsByTagName("dc:date").item(0);
        if (dateElement != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date modifiedDate = dateFormat.parse(dateElement.getTextContent());
                metaData.modified = modifiedDate;

                Log.d(TAG, "Modified Date: " + dateFormat.format(modifiedDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //subject
        NodeList subjectNodeList = document.getElementsByTagName("dc:subject");
        if (subjectNodeList != null) {
            for (int i = 0; i < subjectNodeList.getLength(); i++) {
                Element subjectElement = (Element) subjectNodeList.item(i);
                metaData.subjects.add(new Subject(subjectElement.getTextContent()));

                Log.d(TAG, "Subject: " + subjectElement.getTextContent());
            }
        }

        //language
        NodeList languageNodeList = document.getElementsByTagName("dc:language");
        if (languageNodeList != null) {
            for (int i = 0; i < languageNodeList.getLength(); i++) {
                Element languageElement = (Element) languageNodeList.item(i);
                metaData.languages.add(languageElement.getTextContent());

                Log.d(TAG, "Language: " + languageElement.getTextContent());
            }
        }

        //rights
        NodeList rightNodeList = document.getElementsByTagName("dc:rights");
        if (rightNodeList != null) {
            for (int i = 0; i < rightNodeList.getLength(); i++) {
                Element rightElement = (Element) rightNodeList.item(i);
                metaData.rights.add(rightElement.getTextContent());

                Log.d(TAG, "Rights: " + rightElement.getTextContent());
            }
        }

        //publisher
        NodeList publisherNodeList = document.getElementsByTagName("dc:publisher");
        if (publisherNodeList != null) {
            for (int i = 0; i < publisherNodeList.getLength(); i++) {
                Element publisherElement = (Element) publisherNodeList.item(i);
                metaData.publishers.add(new Contributor(publisherElement.getTextContent()));

                Log.d(TAG, "Publisher: " + publisherElement.getTextContent());
            }
        }

        //creator
        NodeList authorNodeList = document.getElementsByTagName("dc:creator");
        if (authorNodeList != null) {
            for (int i = 0; i < authorNodeList.getLength(); i++) {
                Element authorElement = (Element) authorNodeList.item(i);
                parseContributor(authorElement, document, metaData);
            }
        }

        //contributor
        NodeList contributorNodeList = document.getElementsByTagName("dc:contributor");
        if (contributorNodeList != null) {
            for (int i = 0; i < contributorNodeList.getLength(); i++) {
                Element contributorElement = (Element) contributorNodeList.item(i);
                parseContributor(contributorElement, document, metaData);
            }
        }

        //rendition property
        NodeList metaNodeList = document.getElementsByTagName("meta");
        if (metaNodeList != null) {
            for (int i = 0; i < metaNodeList.getLength(); i++) {
                Element metaElement = (Element) metaNodeList.item(i);
                if (metaElement.getAttribute("property").equals("rendition:layout")) {
                    metaData.rendition.layout = RenditionLayout.valueOfEnum(metaElement.getTextContent());
                }

                if (metaElement.getAttribute("property").equals("rendition:flow")) {
                    metaData.rendition.flow = RenditionFlow.valueOfEnum(metaElement.getTextContent());
                }

                if (metaElement.getAttribute("property").equals("rendition:orientation")) {
                    metaData.rendition.orientation = RenditionOrientation.valueOfEnum(metaElement.getTextContent());
                }

                if (metaElement.getAttribute("property").equals("rendition:spread")) {
                    metaData.rendition.spread = RenditionSpread.valueOfEnum(metaElement.getTextContent());
                }

                if (metaElement.getAttribute("property").equals("rendition:viewport")) {
                    metaData.rendition.viewport = metaElement.getTextContent();
                }
            }
        }

        Element spineElement = (Element) document.getElementsByTagName("spine").item(0);
        if (spineElement != null) {
            metaData.direction = spineElement.getAttribute("page-progression-direction");
        }

        publication.metadata = metaData;

        //cover
        String coverId = null;
        if (metaNodeList != null) {
            for (int i = 0; i < metaNodeList.getLength(); i++) {
                Element metaElement = (Element) metaNodeList.item(i);
                if (metaElement.getAttribute("name").equals("cover")) {
                    //coverId = metaElement.getTextContent();
                    coverId = metaElement.getAttribute("content");
                }
            }
        }

        parseSpineAndResourcesAndGuide(document, publication, coverId, rootFile);

        return publication;
    }

    @Nullable
    private Document xmlParser(String xmlData) throws EpubParserException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlData)));
            document.getDocumentElement().normalize();
            if (document == null) {
                throw new EpubParserException("Error while parsing xml file");
            }

            return document;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private String parseMainTitle(Document document) {
        Element titleElement;
        NodeList titleNodes = document.getElementsByTagName("dc:title");
        if (titleNodes != null) {
            if (titleNodes.getLength() > 1) {
                for (int i = 0; i < titleNodes.getLength(); i++) {
                    titleElement = (Element) titleNodes.item(i);
                    String titleId = titleElement.getAttribute("id");
                    NodeList metaNodes = document.getElementsByTagName("meta");
                    if (metaNodes != null) {
                        for (int j = 0; j < metaNodes.getLength(); j++) {
                            Element metaElement = (Element) metaNodes.item(j);
                            if (metaElement.getAttribute("property").equals("title-type")) {
                                if (metaElement.getAttribute("refines").equals("#" + titleId)) {
                                    if (metaElement.getTextContent().equals("main")) {
                                        return titleElement.getTextContent();
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                titleElement = (Element) titleNodes.item(0);
                return titleElement.getTextContent();
            }
        }
        return null;
    }

    @Nullable
    private String parseUniqueIdentifier(Document document) {
        Element identifierElement;
        NodeList identifierNodes = document.getElementsByTagName("dc:identifier");
        if (identifierNodes != null) {
            if (identifierNodes.getLength() > 1) {
                for (int i = 0; i < identifierNodes.getLength(); i++) {
                    identifierElement = (Element) identifierNodes.item(i);
                    String uniqueId = identifierElement.getAttribute("unique-identifier");
                    if (identifierElement.getAttribute("id").equals(uniqueId)) {
                        return identifierElement.getTextContent();
                    }
                }
            } else {
                identifierElement = (Element) identifierNodes.item(0);
                return identifierElement.getTextContent();
            }
        }
        return null;
    }

    private void parseContributor(Element element, Document document, MetaData metaData) {
        Contributor contributor = createContributorFromElement(element, document);
        if (contributor != null) {
            String role = contributor.getRole();
            if (role != null) {
                switch (role) {
                    case "aut":
                        metaData.creators.add(contributor);
                        break;
                    case "trl":
                        metaData.translators.add(contributor);
                        break;
                    case "art":
                        metaData.artists.add(contributor);
                        break;
                    case "edt":
                        metaData.editors.add(contributor);
                        break;
                    case "ill":
                        metaData.illustrators.add(contributor);
                        break;
                    case "clr":
                        metaData.colorists.add(contributor);
                        break;
                    case "nrt":
                        metaData.narrators.add(contributor);
                        break;
                    case "pbl":
                        metaData.publishers.add(contributor);
                        break;
                    default:
                        metaData.contributors.add(contributor);
                        break;
                }
            } else {
                if (element.getTagName().equals("dc:creator")) {
                    metaData.creators.add(contributor);
                } else {
                    metaData.contributors.add(contributor);
                }
            }
        }
    }

    @Nullable
    private Contributor createContributorFromElement(Element element, Document document) {
        Contributor contributor = new Contributor(element.getTextContent());
        if (contributor != null) {
            if (element.hasAttribute("opf:role")) {
                String role = element.getAttribute("opf:role");
                if (role != null) {
                    contributor.role = role;
                }
            }
            if (element.hasAttribute("opf:file-as")) {
                String sortAs = element.getAttribute("opf:file-as");
                if (sortAs != null) {
                    contributor.sortAs = sortAs;
                }
            }
            if (element.hasAttribute("id")) {
                String identifier = element.getAttribute("id");
                if (identifier != null) {
                    NodeList metas = document.getElementsByTagName("meta");
                    if (metas != null) {
                        for (int i = 0; i < metas.getLength(); i++) {
                            Element metaElement = (Element) metas.item(i);
                            if (metaElement.getAttribute("property").equals("role")) {
                                if (metaElement.getAttribute("refines").equals("#" + identifier)) {
                                    contributor.role = metaElement.getTextContent();
                                }
                            }
                        }
                    }
                }
            }
            return contributor;
        }
        return null;
    }

    private void parseSpineAndResourcesAndGuide(Document document, EpubPublication publication, String coverId, String rootFile) throws EpubParserException {
        int startIndex = 0;
        int endIndex = rootFile.indexOf("/");
        String packageName = rootFile.substring(startIndex, endIndex);

        Map<String, Link> manifestLinks = new HashMap<>();

        NodeList itemNodes = document.getElementsByTagName("item");
        if (itemNodes != null) {
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Element itemElement = (Element) itemNodes.item(i);

                Link link = new Link();
                NamedNodeMap nodeMap = itemElement.getAttributes();
                for (int j = 0; j < nodeMap.getLength(); j++) {
                    Attr attr = (Attr) nodeMap.item(j);
                    switch (attr.getNodeName()) {
                        case "href":
                            link.href = packageName + "/" + attr.getNodeValue();
                            break;
                        case "media-type":
                            link.typeLink = attr.getNodeValue();
                            break;
                        case "properties":
                            if (attr.getNodeValue().equals("nav")) {
                                link.rel.add("contents");
                            } else if (attr.getNodeValue().equals("cover-image")) {
                                link.rel.add("cover");
                            } else if (!attr.getNodeValue().equals("nav") && !attr.getNodeValue().equals("cover-image")) {
                                link.properties.add(attr.getNodeValue());
                            }
                            break;
                    }
                }

                String id = itemElement.getAttribute("id");
                link.setId(id);

                if (id.equals(coverId)) {
                    //link.rel.add("cover");

                    publication.coverLink = new Link();
                    publication.coverLink.rel.add("cover");
                    publication.coverLink.setId(id);
                    publication.coverLink.setHref(link.getHref());
                    publication.coverLink.setTypeLink(link.getTypeLink());
                    publication.coverLink.setProperties(link.getProperties());
                }
                //publication.links.add(link);
                publication.linkMap.put(link.href, link);
                manifestLinks.put(id, link);

                if ((link.getTypeLink().equals("application/x-dtbncx+xml")) && (link.getHref().endsWith(".ncx"))) {
                    parseNCXFile(link.getHref(), packageName);
                }
            }
            Log.d(TAG, "Link count: " + publication.linkMap.size());
        }

        NodeList itemRefNodes = document.getElementsByTagName("itemref");
        if (itemRefNodes != null) {
            for (int i = 0; i < itemRefNodes.getLength(); i++) {
                Element itemRefElement = (Element) itemRefNodes.item(i);
                String id = itemRefElement.getAttribute("idref");
                if (manifestLinks.containsKey(id)) {
                    publication.spines.add(manifestLinks.get(id));
                    manifestLinks.remove(id);
                }
            }
            Log.d(TAG, "Spine count: " + publication.spines.size());
        }
        publication.resources.addAll(manifestLinks.values());
        Log.d(TAG, "Resource count: " + publication.resources.size());

        NodeList referenceNodes = document.getElementsByTagName("reference");
        if (referenceNodes != null) {
            for (int i = 0; i < referenceNodes.getLength(); i++) {
                Element referenceElement = (Element) referenceNodes.item(i);
                Link link = new Link();
                link.setType(referenceElement.getAttribute("type"));
                link.setChapterTitle(referenceElement.getAttribute("title"));
                link.setHref(referenceElement.getAttribute("href"));
                publication.guides.add(link);
            }
        }
        Log.d(TAG, "Guide count: " + publication.guides.size());
    }

    private void parseNCXFile(String ncxFile, String packageName) throws EpubParserException {
        String ncxData = container.rawData(ncxFile);
        if (ncxData == null) {
            Log.e(TAG, "File is missing: " + ncxFile);
            throw new EpubParserException("File is missing");
        }
        Document document = xmlParser(ncxData);
        if (document == null) {
            throw new EpubParserException("Error while parsing");
        }

        publication.tableOfContents = new ToC();
        Element docTitleElement = (Element) document.getElementsByTagName("docTitle").item(0);
        if (docTitleElement != null) {
            publication.tableOfContents.setDocTitle(docTitleElement.getTextContent());
        }

        Element navMapElement = (Element) document.getElementsByTagName("navMap").item(0);
        NodeList navPointNodes = navMapElement.getChildNodes();
        if (navPointNodes != null) {
            for (int i = 0; i < navPointNodes.getLength(); i++) {
                Element navPointElement = (Element) navPointNodes.item(i);
                publication.tableOfContents.tocLinks.add(parseNavPointElement(navPointElement, packageName));
            }
            publication.tableOfContents.tocLinks.size();
        }
    }

    @Nullable
    private TOCLink parseNavPointElement(Element element, String packageName) {
        //if (element.getTagName().equals("navPoint")) {
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
                tocLink.setHref(packageName + "/" + parseTOCResourceLink(contentNodes));
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
    //return null;
    //}

    private String parseTOCLabel(NodeList nodeList) {
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

    private String parseTOCResourceLink(NodeList nodeList) {
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element srcElement = (Element) nodeList.item(i);
                return srcElement.getAttribute("src");
            }
        }
        return null;
    }
}