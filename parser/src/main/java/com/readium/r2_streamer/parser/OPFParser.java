package com.readium.r2_streamer.parser;

import com.readium.r2_streamer.model.container.Container;
import com.readium.r2_streamer.model.publication.EpubPublication;
import com.readium.r2_streamer.model.publication.contributor.Contributor;
import com.readium.r2_streamer.model.publication.link.Link;
import com.readium.r2_streamer.model.publication.metadata.MetaData;
import com.readium.r2_streamer.model.publication.metadata.MetadataItem;
import com.readium.r2_streamer.model.publication.rendition.RenditionFlow;
import com.readium.r2_streamer.model.publication.rendition.RenditionLayout;
import com.readium.r2_streamer.model.publication.rendition.RenditionOrientation;
import com.readium.r2_streamer.model.publication.rendition.RenditionSpread;
import com.readium.r2_streamer.model.publication.subject.Subject;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gautam chibde on 31/5/17.
 */

public class OPFParser {

    private static final String TAG = OPFParser.class.getSimpleName();

    public static EpubPublication parseOpfFile(String rootFile, EpubPublication publication, Container container) throws EpubParserException {
        String opfData = container.rawData(rootFile);
        if (opfData == null) {
            System.out.println(TAG + "File is missing: " + rootFile);
            throw new EpubParserException("File is missing");
        }

        Document document = EpubParser.xmlParser(opfData);
        if (document == null) {
            throw new EpubParserException("Error while parsing");
        }


        MetaData metaData = new MetaData();

        //title
        metaData.title = parseMainTitle(document);
        //Log.d(TAG, "Title: " + metaData.getTitle());

        //identifier
        metaData.identifier = parseUniqueIdentifier(document);
        //Log.d(TAG, "Identifier: " + metaData.getIdentifier());

        //description
        Element descriptionElement = (Element) ((Element) document.getDocumentElement().getElementsByTagName("metadata").item(0)).getElementsByTagName("dc:description").item(0);
        if (descriptionElement != null) {
            metaData.description = descriptionElement.getTextContent();
        }
        //Log.d(TAG, "Description: " + metaData.getDescription());

        //modified date
        Element dateElement = (Element) ((Element) document.getDocumentElement().getElementsByTagName("metadata").item(0)).getElementsByTagName("dc:date").item(0);
        if (dateElement != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date modifiedDate = dateFormat.parse(dateElement.getTextContent());
                metaData.modified = modifiedDate;

                //Log.d(TAG, "Modified Date: " + dateFormat.format(modifiedDate));
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

                //Log.d(TAG, "Subject: " + subjectElement.getTextContent());
            }
        }

        //language
        NodeList languageNodeList = document.getElementsByTagName("dc:language");
        if (languageNodeList != null) {
            for (int i = 0; i < languageNodeList.getLength(); i++) {
                Element languageElement = (Element) languageNodeList.item(i);
                metaData.languages.add(languageElement.getTextContent());

                //Log.d(TAG, "Language: " + languageElement.getTextContent());
            }
        }

        //rights
        NodeList rightNodeList = document.getElementsByTagName("dc:rights");
        if (rightNodeList != null) {
            for (int i = 0; i < rightNodeList.getLength(); i++) {
                Element rightElement = (Element) rightNodeList.item(i);
                metaData.rights.add(rightElement.getTextContent());

                //Log.d(TAG, "Rights: " + rightElement.getTextContent());
            }
        }

        //publisher
        NodeList publisherNodeList = document.getElementsByTagName("dc:publisher");
        if (publisherNodeList != null) {
            for (int i = 0; i < publisherNodeList.getLength(); i++) {
                Element publisherElement = (Element) publisherNodeList.item(i);
                metaData.publishers.add(new Contributor(publisherElement.getTextContent()));

                //Log.d(TAG, "Publisher: " + publisherElement.getTextContent());
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
                if (metaElement.getAttribute("property").equals("media:duration")) {
                    MetadataItem metadataItem = new MetadataItem();
                    metadataItem.property = metaElement.getAttribute("refines");
                    metadataItem.value = metaElement.getTextContent();
                    metaData.getOtherMetadata().add(metadataItem);
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
        parseSpineAndResourcesAndGuide(document, publication, coverId, rootFile, container);
        return publication;
    }

    //@Nullable
    private static String parseMainTitle(Document document) {
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

    //@Nullable
    private static String parseUniqueIdentifier(Document document) {
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

    private static void parseContributor(Element element, Document document, MetaData metaData) {
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

    //@Nullable
    private static Contributor createContributorFromElement(Element element, Document document) {
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

    private static void parseSpineAndResourcesAndGuide(Document document, EpubPublication publication, String coverId, String rootFile, Container container) throws EpubParserException {
        int startIndex = 0;
        int endIndex = rootFile.indexOf("/");
        System.out.println(TAG + " rootFile:= " + rootFile);
        String packageName = "";
        if (endIndex != -1) {
            packageName = rootFile.substring(startIndex, endIndex) + "/";
        }
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
                            link.href = packageName + attr.getNodeValue();
                            break;
                        case "media-type":
                            link.typeLink = attr.getNodeValue();
                            if (link.typeLink.equalsIgnoreCase("application/smil+xml")) {
                                link.duration = MetadataItem.getSMILDuration(publication.metadata.getOtherMetadata(), link.id);
                            }
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
                if (id.equalsIgnoreCase("ncx")) {
                    NCXParser.parseNCXFile(link.getHref(), container, publication);
                }
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
            }
            //Log.d(TAG, "Link count: " + publication.linkMap.size());
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
            //Log.d(TAG, "Spine count: " + publication.spines.size());
        }
        publication.resources.addAll(manifestLinks.values());
        //Log.d(TAG, "Resource count: " + publication.resources.size());

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
        //Log.d(TAG, "Guide count: " + publication.guides.size());
    }
}
