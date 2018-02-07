package org.readium.r2_streamer.parser;

import org.readium.r2_streamer.model.container.Container;
import org.readium.r2_streamer.model.publication.EpubPublication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

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
        this.publication = new EpubPublication();
    }

    public EpubPublication parseEpubFile(String filePath) {
        String rootFile;
        try {
            if (filePath.contains(".cbz")) {
                CBZParser.parseCBZ(container, publication);
                return publication;
            }
            if (isMimeTypeValid()) {
                rootFile = parseContainer();

                publication.internalData.put("type", "epub");
                publication.internalData.put("rootfile", rootFile);
                //Parse OPF file
                this.publication = OPFParser.parseOpfFile(rootFile, this.publication, container);
                // Parse Encryption
                this.publication.encryptions = EncryptionParser.parseEncryption(container);
                // Parse Media Overlay
                MediaOverlayParser.parseMediaOverlay(this.publication, container);
                return publication;
            }
        } catch (EpubParserException e) {
            System.out.println(TAG + " parserEpubFile() error " + e.toString());
        }
        return null;
    }

    private boolean isMimeTypeValid() throws EpubParserException {
        String mimeTypeData = container.rawData("mimetype");

        if (mimeTypeData.equals("application/epub+zip")) {
            return true;
        } else {
            System.out.println(TAG + "Invalid MIME type: " + mimeTypeData);
            throw new EpubParserException("Invalid MIME type");
        }
    }

    private String parseContainer() throws EpubParserException {
        String containerPath = "META-INF/container.xml";
        String containerData = container.rawData(containerPath);

        if (containerData == null) {
            System.out.println(TAG + " File is missing: " + containerPath);
            throw new EpubParserException("File is missing");
        }

        String opfFile = containerXmlParser(containerData);
        if (opfFile == null) {
            throw new EpubParserException("Error while parsing");
        }
        return opfFile;
    }

    //@Nullable
    private String containerXmlParser(String containerData) throws EpubParserException {           //parsing container.xml
        try {
            String xml = containerData.replaceAll("[^\\x20-\\x7e]", "").trim();         //in case encoding problem

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new InputSource(new StringReader(xml)));
            document.getDocumentElement().normalize();

            if (document == null) {
                throw new EpubParserException("Error while parsing container.xml");
            }

            Element rootElement = (Element) ((Element) document.getDocumentElement().getElementsByTagNameNS("*", "rootfiles").item(0)).getElementsByTagNameNS("*", "rootfile").item(0);
            if (rootElement != null) {
                String opfFile = rootElement.getAttribute("full-path");
                if (opfFile == null) {
                    throw new EpubParserException("Missing root file element in container.xml");
                }

                return opfFile;                    //returns opf file
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //@Nullable
    public static Document xmlParser(String xmlData) throws EpubParserException {
        try {
            xmlData = xmlData.replaceAll("[^\\x20-\\x7e]", "").trim();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();

            StringReader characterStream = new StringReader(xmlData);
            InputSource inputSource = new InputSource(characterStream);
            Document document = builder.parse(inputSource);
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

}