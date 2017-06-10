package org.readium.r2_streamer.model.publication.rendition;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public enum RenditionFlow {
    PAGINATED("paginated"), CONTINUOUS("continuous"), DOCUMENT("document"), FIXED("fixed");

    String value;

    RenditionFlow(String value) {
        this.value = value;
    }

    public static RenditionFlow valueOfEnum(String name) {
        for (RenditionFlow layout : RenditionFlow.values()) {
            if (layout.value.equals(name)) {
                return layout;
            }
        }
        throw new IllegalArgumentException(name);
    }
}
