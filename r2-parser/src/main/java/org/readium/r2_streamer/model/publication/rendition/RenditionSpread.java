package org.readium.r2_streamer.model.publication.rendition;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public enum RenditionSpread {
    AUTO("auto"), LANDSCAPE("landscape"), PORTRAIT("portrait"), BOTH("both"), NONE("none");

    String value;

    RenditionSpread(String value) {
        this.value = value;
    }

    public static RenditionSpread valueOfEnum(String name) {
        for (RenditionSpread layout : RenditionSpread.values()) {
            if (layout.value.equals(name)) {
                return layout;
            }
        }
        throw new IllegalArgumentException(name);
    }
}
