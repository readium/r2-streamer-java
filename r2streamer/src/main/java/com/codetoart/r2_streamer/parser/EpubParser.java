package com.codetoart.r2_streamer.parser;

import android.support.annotation.Nullable;
import android.util.Log;

import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.model.publication.EpubPublication;
import com.codetoart.r2_streamer.model.publication.contributor.Contributor;
import com.codetoart.r2_streamer.model.publication.link.Link;
import com.codetoart.r2_streamer.model.publication.metadata.MetaData;
import com.codetoart.r2_streamer.model.publication.rendition.RenditionFlow;
import com.codetoart.r2_streamer.model.publication.rendition.RenditionLayout;
import com.codetoart.r2_streamer.model.publication.rendition.RenditionOrientation;
import com.codetoart.r2_streamer.model.publication.rendition.RenditionSpread;
import com.codetoart.r2_streamer.model.publication.subject.Subject;
import com.codetoart.r2_streamer.model.tableofcontents.NCX;
import com.codetoart.r2_streamer.model.tableofcontents.TOCLink;

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
        NodeList subjectNodes = document.getElementsByTagName("dc:subject");
        if (subjectNodes != null) {
            for (int i = 0; i < subjectNodes.getLength(); i++) {
                Element subjectElement = (Element) subjectNodes.item(i);
                metaData.subjects.add(new Subject(subjectElement.getTextContent()));

                Log.d(TAG, "Subject: " + subjectElement.getTextContent());
            }
        }

        //language
        NodeList languageNodes = document.getElementsByTagName("dc:language");
        if (languageNodes != null) {
            for (int i = 0; i < languageNodes.getLength(); i++) {
                Element languageElement = (Element) languageNodes.item(i);
                metaData.languages.add(languageElement.getTextContent());

                Log.d(TAG, "Language: " + languageElement.getTextContent());
            }
        }

        //rights
        NodeList rightNodes = document.getElementsByTagName("dc:rights");
        if (rightNodes != null) {
            for (int i = 0; i < rightNodes.getLength(); i++) {
                Element rightElement = (Element) rightNodes.item(i);
                metaData.rights.add(rightElement.getTextContent());

                Log.d(TAG, "Rights: " + rightElement.getTextContent());
            }
        }

        //publisher
        NodeList publisherNodes = document.getElementsByTagName("dc:publisher");
        if (publisherNodes != null) {
            for (int i = 0; i < publisherNodes.getLength(); i++) {
                Element publisherElement = (Element) publisherNodes.item(i);
                metaData.publishers.add(new Contributor(publisherElement.getTextContent()));

                Log.d(TAG, "Publisher: " + publisherElement.getTextContent());
            }
        }

        //creator
        NodeList authorNodes = document.getElementsByTagName("dc:creator");
        if (authorNodes != null) {
            for (int i = 0; i < authorNodes.getLength(); i++) {
                Element authorElement = (Element) authorNodes.item(i);
                parseContributor(authorElement, document, metaData);
            }
        }

        //contributor
        NodeList contributorNodes = document.getElementsByTagName("dc:contributor");
        if (contributorNodes != null) {
            for (int i = 0; i < contributorNodes.getLength(); i++) {
                Element contributorElement = (Element) contributorNodes.item(i);
                parseContributor(contributorElement, document, metaData);
            }
        }

        //rendition property
        NodeList metaNodes = document.getElementsByTagName("meta");
        if (metaNodes != null) {
            for (int i = 0; i < metaNodes.getLength(); i++) {
                Element metaElement = (Element) metaNodes.item(i);
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
        if (metaNodes != null) {
            for (int i = 0; i < metaNodes.getLength(); i++) {
                Element metaElement = (Element) metaNodes.item(i);
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
    private Document xmlParser(String opfData) throws EpubParserException {                     //parsing content.opf
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(opfData)));
            document.getDocumentElement().normalize();
            if (document == null) {
                throw new EpubParserException("Error while parsing content.opf");
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
                    parseNCXFile(link.getHref());
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

    private void parseNCXFile(String ncxFile) throws EpubParserException {
        String ncxData = container.rawData(ncxFile);
        if (ncxData == null) {
            Log.e(TAG, "File is missing: " + ncxFile);
            throw new EpubParserException("File is missing");
        }
        Document document = xmlParser(ncxData);
        if (document == null) {
            throw new EpubParserException("Error while parsing");
        }

        publication.tableOfContents = new NCX();
        Element docTitleElement = (Element) document.getElementsByTagName("docTitle").item(0);
        if (docTitleElement != null) {
            publication.tableOfContents.setDocTitle(docTitleElement.getTextContent());
        }

        NodeList navPointNodes = document.getElementsByTagName("navPoint");
        if (navPointNodes != null) {
            for (int i = 0; i < navPointNodes.getLength(); i++) {
                Element navPoint = (Element) navPointNodes.item(i);
                TOCLink tocLink = parseElement(parseNode(navPoint));
                publication.tableOfContents.tocLinks.add(tocLink);
            }
            publication.tableOfContents.tocLinks.size();
        }

                /*if (navPoint.getTagName().equals("navPoint") && navPoint.hasChildNodes()) {
                    NodeList childNodes_1 = navPoint.getChildNodes();
                    for (int j = 0; j < childNodes_1.getLength(); j++) {
                        Element childElement_1 = (Element) childNodes_1.item(j);
                        if (childElement_1.getTagName().equals("navPoint") && childElement_1.hasChildNodes()) {
                            NodeList childNodes_2 = childElement_1.getChildNodes();
                            for (int k = 0; k < childNodes_2.getLength(); k++) {
                                Element childElement_2 = (Element) childNodes_2.item(k);
                                if (childElement_2.getTagName().equals("navPoint") && childElement_2.hasChildNodes()) {
                                    tocLink = parseElement(childElement_2);
                                    tocLinksList.add(tocLink);
                                }
                                tocLink.setTocLinks(tocLinksList);
                                publication.tableOfContents.tocLinks.add(tocLink);
                            }
                        } else {
                            break;
                        }
                        tocLink = parseElement(childElement_1);
                        tocLinksList.add(tocLink);
                    }
                    tocLink.setTocLinks(tocLinksList);
                    publication.tableOfContents.tocLinks.add(tocLink);
                } else {
                    break;
                }
                tocLink = parseElement(navPoint);
                publication.tableOfContents.tocLinks.add(tocLink);*/
    }

    private Element parseNode(Element element) {
        if (element.hasChildNodes()) {
            NodeList childNodeList = element.getChildNodes();
            for (int i = 0; i < childNodeList.getLength(); i++) {
                Node childNode = childNodeList.item(i);
                if (childNode.getNodeName().equals("navPoint")) {
                    Element childElement = (Element) childNode;
                    parseNode(childElement);
                }
            }
        }
        return element;
    }

    @Nullable
    private TOCLink parseElement(Element element) {
        if (element.getTagName().equals("navPoint")) {
            TOCLink tocLink = new TOCLink();
            tocLink.setId(element.getAttribute("id"));
            tocLink.setPlayOrder(element.getAttribute("playOrder"));

            NodeList labelNodes = element.getElementsByTagName("navLabel");
            if (labelNodes != null) {
                for (int i = 0; i < labelNodes.getLength(); i++) {
                    Element label = (Element) labelNodes.item(i);
                    NodeList textNodes = label.getElementsByTagName("text");
                    if (textNodes != null) {
                        for (int k = 0; k < textNodes.getLength(); k++) {
                            Element text = (Element) textNodes.item(k);
                            tocLink.setSectionTitle(text.getTextContent());
                        }
                    }
                }
            }

            NodeList contentNodes = element.getElementsByTagName("content");
            for (int j = 0; j < contentNodes.getLength(); j++) {
                Element content = (Element) contentNodes.item(j);
                tocLink.setHref(content.getAttribute("src"));
            }

            return tocLink;
        }
        return null;
    }
}