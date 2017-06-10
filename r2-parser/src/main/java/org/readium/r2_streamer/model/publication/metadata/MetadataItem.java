package org.readium.r2_streamer.model.publication.metadata;

import org.readium.r2_streamer.model.publication.SMIL.SMILParser;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class MetadataItem implements Serializable {
    private static final long serialVersionUID = 7526471195622776147L;
    public String property;
    public String value;
    public List<MetadataItem> children;

    public MetadataItem() {
    }

    public MetadataItem(String property, String value, List<MetadataItem> children) {
        this.property = property;
        this.value = value;
        this.children = children;
    }

    @Override
    public String toString() {
        return "MetadataItem{" +
                "property='" + property + '\'' +
                ", value='" + value + '\'' +
                ", children=" + children +
                '}';
    }

    public static String getSMILDuration(List<MetadataItem> otherMetadata, String id) {
        for (MetadataItem metadataItem : otherMetadata) {
            if (metadataItem.property.equalsIgnoreCase("#" + id)) {
                return SMILParser.smilTimeToSeconds(metadataItem.value);
            }
        }
        return null;
    }
}