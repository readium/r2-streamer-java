package org.readium.r2_streamer.model.publication.rendition;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public enum RenditionLayout {
    REFLOWABLE("reflowable"), PREPAGINATED("pre-paginated");

    String value;

    RenditionLayout(String value) {
        this.value = value;
    }

    public static RenditionLayout valueOfEnum(String name) {
        for (RenditionLayout layout : RenditionLayout.values()) {
            if (layout.value.equals(name)) {
                return layout;
            }
        }
        throw new IllegalArgumentException(name);
    }
}
