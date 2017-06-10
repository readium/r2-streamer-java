package org.readium.r2_streamer.model.publication.rendition;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public enum RenditionOrientation {
    AUTO("auto"), LANDSCAPE("landscape"), PORTRAIT("portrait");

    String value;

    RenditionOrientation(String value) {
        this.value = value;
    }

    public static RenditionOrientation valueOfEnum(String name) {
        for (RenditionOrientation layout : RenditionOrientation.values()) {
            if (layout.value.equals(name)) {
                return layout;
            }
        }
        throw new IllegalArgumentException(name);
    }
}
