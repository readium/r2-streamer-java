package com.readium.r2_streamer.parser;

import com.readium.r2_streamer.model.publication.Encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;

import javax.xml.bind.DatatypeConverter;

/**
 * Created by gautam chibde on 18/5/17.
 */

public final class Decoder {
    private final static String TAG = Decoder.class.getName();

    private static HashMap<String, Integer> decoders = new HashMap<>();

    static {
        com.readium.r2_streamer.parser.Decoder.decoders.put("http://www.idpf.org/2008/embedding", 1040);
        com.readium.r2_streamer.parser.Decoder.decoders.put("http://ns.adobe.com/pdf/enc#RC", 1024);
    }

    /**
     * Decode obfuscated font from a InputStream, if the encryption is known.
     *
     * @param identifier The associated publication Identifier.
     * @param inStream   font input stream
     * @param encryption {@link Encryption} object contains encryption type
     * @return The InputStream containing the unencrypted resource.
     */
    public static InputStream decode(String identifier, InputStream inStream, Encryption encryption) {
        if (identifier == null) {
            System.out.println(TAG + " Couldn't get the publication identifier.");
            return inStream;
        }

        if (!decoders.containsKey(encryption.getAlgorithm())) {
            System.out.println(TAG + " " + encryption.getProfile() + " is encrypted but decoder cant handle it");
            return inStream;
        }
        return decodeFont(identifier, inStream, decoders.get(encryption.getAlgorithm()), encryption.getAlgorithm());
    }

    /**
     * Decode the given inputStream first X characters, depending of the obfuscation type.
     *
     * @param identifier The associated publication Identifier.
     * @param inStream   The input stream containing the data of an obfuscated font
     * @param length     The ObfuscationLength depending of the obfuscation type.
     * @param algorithm  type of algorithm
     * @return The Deobfuscated InputStream.
     */
    private static InputStream decodeFont(String identifier, InputStream inStream, Integer length, String algorithm) {

        byte[] publicationKey = null;
        switch (algorithm) {
            case "http://ns.adobe.com/pdf/enc#RC":
                publicationKey = getHashKeyAdobe(identifier);
                break;
            case "http://www.idpf.org/2008/embedding":
                publicationKey = getHashKeyIdpf(identifier);
                break;
        }
        return deobfuscate(inStream, publicationKey, length);
    }

    /**
     * Receive an obfuscated InputStream and return deabfuscated InputStream
     *
     * @param inStream       The input stream containing the data of an obfuscated font
     * @param publicationKey The publicationKey used to decode the X first characters.
     * @param length         The number of characters obfuscated at the first of the file.
     * @return The Deobfuscated InputStream.
     */
    private static InputStream deobfuscate(InputStream inStream, byte[] publicationKey, Integer length) {
        if (publicationKey == null) {
            return inStream;
        }
        try {
            byte[] bytes = new byte[inStream.available()];
            int size = inStream.read(bytes);
            int count = size > length ? length : size;
            int pubKeyLength = publicationKey.length;
            int i = 0;
            while (i < count) {
                bytes[i] = (byte) (bytes[i] ^ (publicationKey[i % pubKeyLength]));
                i++;
            }
            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            System.out.println(TAG + ".deobfuscate() " + e);
        }

        return null;
    }

    /**
     * Create an EPUB font obfuscation key from one or more strings according to the rules
     * defined in the EPUB 3 spec, 4.3 Generating the Obfuscation Key
     * (http://www.idpf.org/epub/30/spec/epub30-ocf.html#fobfus-keygen)
     * <p>
     * Squeezes out any whitespace in each UID and then concatenates the result
     * using single space characters as the separator.
     *
     * @param identifier The string to convert into a key.
     * @return obfuscation key string
     */
    private static byte[] getHashKeyIdpf(String identifier) {
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(identifier.getBytes("UTF-8"));
            return hexaToBytes(byteToHex(crypt.digest()));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Generate the Hashkey used to salt the 1024 starting character of the Adobe font files.
     *
     * @param pubId The publication Identifier.
     * @return The key's bytes array.
     */
    private static byte[] getHashKeyAdobe(String pubId) {
        String cleanPubId = pubId.replaceAll("urn:uuid", "");
        cleanPubId = cleanPubId.replaceAll("-", "");
        return hexaToBytes(cleanPubId);
    }

    /**
     * Convert hexadecimal String to Bytes (UInt8) array.
     *
     * @param s The hexadecimal String
     * @return The key's bytes array.
     */
    private static byte[] hexaToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Convert Bytes array to hexadecimal String.
     *
     * @param hash bytes array.
     * @return The hexadecimal String.
     */
    private static String byteToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
