package com.codetoart.r2_streamer.parser;

import android.support.annotation.Nullable;
import android.util.Log;

import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.model.publication.Contributor;
import com.codetoart.r2_streamer.model.publication.EpubPublication;
import com.codetoart.r2_streamer.model.publication.Link;
import com.codetoart.r2_streamer.model.publication.MetaData;
import com.codetoart.r2_streamer.model.publication.Subject;
import com.codetoart.r2_streamer.model.publication.rendition.RenditionFlow;
import com.codetoart.r2_streamer.model.publication.rendition.RenditionLayout;
import com.codetoart.r2_streamer.model.publication.rendition.RenditionOrientation;
import com.codetoart.r2_streamer.model.publication.rendition.RenditionSpread;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

        Document document = opfXmlParser(opfData);
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
        NodeList subjects = document.getElementsByTagName("dc:subject");
        if (subjects != null) {
            for (int i = 0; i < subjects.getLength(); i++) {
                Element subjectElement = (Element) subjects.item(i);
                metaData.subjects.add(new Subject(subjectElement.getTextContent()));

                Log.d(TAG, "Subject: " + subjectElement.getTextContent());
            }
        }

        //language
        NodeList languages = document.getElementsByTagName("dc:language");
        if (languages != null) {
            for (int i = 0; i < languages.getLength(); i++) {
                Element languageElement = (Element) languages.item(i);
                metaData.languages.add(languageElement.getTextContent());

                Log.d(TAG, "Language: " + languageElement.getTextContent());
            }
        }

        //rights
        NodeList rights = document.getElementsByTagName("dc:rights");
        if (rights != null) {
            for (int i = 0; i < rights.getLength(); i++) {
                Element rightElement = (Element) rights.item(i);
                metaData.rights.add(rightElement.getTextContent());

                Log.d(TAG, "Rights: " + rightElement.getTextContent());
            }
        }

        //publisher
        NodeList publishers = document.getElementsByTagName("dc:publisher");
        if (publishers != null) {
            for (int i = 0; i < publishers.getLength(); i++) {
                Element publisherElement = (Element) publishers.item(i);
                metaData.publishers.add(new Contributor(publisherElement.getTextContent()));

                Log.d(TAG, "Publisher: " + publisherElement.getTextContent());
            }
        }

        //creator
        NodeList authors = document.getElementsByTagName("dc:creator");
        if (authors != null) {
            for (int i = 0; i < authors.getLength(); i++) {
                Element authorElement = (Element) authors.item(i);
                parseContributor(authorElement, document, metaData);
            }
        }

        //contributor
        NodeList contributors = document.getElementsByTagName("dc:contributor");
        if (contributors != null) {
            for (int i = 0; i < contributors.getLength(); i++) {
                Element contributorElement = (Element) contributors.item(i);
                parseContributor(contributorElement, document, metaData);
            }
        }

        //rendition property
        NodeList metas = document.getElementsByTagName("meta");
        if (metas != null) {
            for (int i = 0; i < metas.getLength(); i++) {
                Element metaElement = (Element) metas.item(i);
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
        if (metas != null) {
            for (int i = 0; i < metas.getLength(); i++) {
                Element metaElement = (Element) metas.item(i);
                if (metaElement.getAttribute("name").equals("cover")) {
                    //coverId = metaElement.getTextContent();
                    coverId = metaElement.getAttribute("content");
                }
            }
        }

        parseSpineAndResources(document, publication, coverId, rootFile);

        return publication;
    }

    @Nullable
    private Document opfXmlParser(String opfData) throws EpubParserException {                     //parsing content.opf
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(opfData)));
            document.getDocumentElement().normalize();
            if (document == null) {
                throw new EpubParserException("Error while parsing content.opf");
            }

            return document;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private String parseMainTitle(Document document) {
        Element titleElement;
        NodeList titles = document.getElementsByTagName("dc:title");
        if (titles != null) {
            if (titles.getLength() > 1) {
                for (int i = 0; i < titles.getLength(); i++) {
                    titleElement = (Element) titles.item(i);
                    String titleId = titleElement.getAttribute("id");
                    NodeList metas = document.getElementsByTagName("meta");
                    if (metas != null) {
                        for (int j = 0; j < metas.getLength(); j++) {
                            Element metaElement = (Element) metas.item(j);
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
                titleElement = (Element) titles.item(0);
                return titleElement.getTextContent();
            }
        }
        return null;
    }

    @Nullable
    private String parseUniqueIdentifier(Document document) {
        Element identifierElement;
        NodeList identifiers = document.getElementsByTagName("dc:identifier");
        if (identifiers != null) {
            if (identifiers.getLength() > 1) {
                for (int i = 0; i < identifiers.getLength(); i++) {
                    identifierElement = (Element) identifiers.item(i);
                    String uniqueId = identifierElement.getAttribute("unique-identifier");
                    if (identifierElement.getAttribute("id").equals(uniqueId)) {
                        return identifierElement.getTextContent();
                    }
                }
            } else {
                identifierElement = (Element) identifiers.item(0);
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

    private void parseSpineAndResources(Document document, EpubPublication publication, String coverId, String rootFile) {
        int startIndex = 0;
        int endIndex = rootFile.indexOf("/");
        String packageName = rootFile.substring(startIndex, endIndex);

        Map<String, Link> manifestLinks = new HashMap<String, Link>();

        NodeList items = document.getElementsByTagName("item");
        if (items != null) {
            for (int i = 0; i < items.getLength(); i++) {
                Element itemElement = (Element) items.item(i);

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
                if (id.equals(coverId)) {
                    link.rel.add("cover");
                    //publication.links.add(link);
                }
                publication.links.add(link);
                manifestLinks.put(id, link);
            }

            Log.d(TAG, "Link count: " + publication.links.size());
        }

        NodeList itemRefs = document.getElementsByTagName("itemref");
        if (itemRefs != null) {
            for (int i = 0; i < itemRefs.getLength(); i++) {
                Element itemRefElement = (Element) itemRefs.item(i);
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
    }
}