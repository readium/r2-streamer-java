package com.codetoart.r2_streamer.model.publication;

import java.util.List;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class MetadataItem {
    public String property;
    public String value;
    //public List<MetadataItem> children;
    public MetadataItem[] children;

    public MetadataItem() {
    }

    public MetadataItem(String property, String value, MetadataItem[] children) {
        this.property = property;
        this.value = value;
        this.children = children;
    }
}
