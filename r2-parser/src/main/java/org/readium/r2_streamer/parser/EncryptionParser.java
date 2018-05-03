package org.readium.r2_streamer.parser;

import org.readium.r2_streamer.model.container.Container;
import org.readium.r2_streamer.model.publication.Encryption;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gautam chibde on 31/5/17.
 */

public class EncryptionParser {

    private static final String TAG = EncryptionParser.class.getSimpleName();

    /**
     * parse file encryption.xml located at META-INF/encryption.xml
     * <p>
     * content of the encryption file are saved as {@link Encryption} where path to
     * encrypted file is saved in {@link Encryption#profile} and
     * encryption algorithm in {@link Encryption#algorithm}
     */
    public static List<Encryption> parseEncryption(Container container) {
        String containerPath = "META-INF/encryption.xml";
        try {
            String containerData = container.rawData(containerPath);
            if (containerData == null || containerData.isEmpty())
                throw new FileNotFoundException();
            Document encryptionDocument = EpubParser.xmlParser(containerData);
            if (encryptionDocument == null) {
                throw new EpubParserException("Error while paring META-INF/encryption.xml");
            }
            NodeList element = encryptionDocument.getDocumentElement().getElementsByTagNameNS("*", "EncryptedData");

            List<Encryption> encryptions = new ArrayList<>();
            for (int i = 0; i < element.getLength(); i++) {
                Encryption encryption = new Encryption();
                Element algorithmElement = (Element) ((Element) element.item(i)).getElementsByTagNameNS("*", "EncryptionMethod").item(0);
                Element pathElement = (Element) ((Element) ((Element) element.item(i)).getElementsByTagNameNS("*", "CipherData").item(0)).getElementsByTagNameNS("*", "CipherReference").item(0);
                if (algorithmElement != null) {
                    if (algorithmElement.hasAttribute("Algorithm")) {
                        encryption.setAlgorithm(algorithmElement.getAttribute("Algorithm"));
                    }
                }
                if (pathElement != null) {
                    if (pathElement.hasAttribute("URI")) {
                        encryption.setProfile(pathElement.getAttribute("URI"));
                    }
                }
                //TODO properties
                //TODO LCP
                encryptions.add(encryption);
            }
            return encryptions;
        } catch (EpubParserException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException | NullPointerException e) {
            System.out.println(TAG + " META-INF/encryption.xml not found " + e);
            return null;
        }
    }
}
