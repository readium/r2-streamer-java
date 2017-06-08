package org.readium.r2_streamer.model.publication;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gautam chibde on 18/5/17.
 */

public class Encryption implements Serializable {
    private static final long serialVersionUID = 333647343242776147L;

    private String scheme;
    private String profile;
    private String algorithm;
    private String compression;
    private int originalLength;

    public Encryption() {
    }

    @Override
    public String toString() {
        return "Encryption{" +
                "scheme='" + scheme + '\'' +
                ", profile='" + profile + '\'' +
                ", algorithm='" + algorithm + '\'' +
                ", compression='" + compression + '\'' +
                ", originalLength=" + originalLength +
                '}';
    }

    public static Encryption getEncryptionFormFontFilePath(
            String path,
            List<Encryption> encryptions) {
        for (Encryption encryption : encryptions) {
            if (encryption.getProfile().equalsIgnoreCase(path)) {
                return encryption;
            }
        }
        return null;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public int getOriginalLength() {
        return originalLength;
    }

    public void setOriginalLength(int originalLength) {
        this.originalLength = originalLength;
    }
}
