package org.readium.r2_streamer.model.publication;

import org.readium.r2_streamer.model.publication.link.Link;
import org.readium.r2_streamer.model.publication.metadata.MetaData;
import org.readium.r2_streamer.model.tableofcontents.TOCLink;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class EpubPublication implements Serializable {
    private static final long serialVersionUID = 3336472295622776147L;

    @JsonProperty("metadata")
    public MetaData metadata;

    @JsonIgnore
    public HashMap<String, Link> linkMap;
    @JsonProperty("links")
    public List<Link> links;
    @JsonIgnore
    public List<Link> matchingLinks;
    @JsonProperty("spines")
    public List<Link> spines;
    @JsonProperty("resources")
    public List<Link> resources;
    @JsonIgnore
    public List<Link> guides;

    @JsonIgnore
    public List<Encryption> encryptions;

    //public List<Link> pageList;
    @JsonProperty("toc")
    public List<TOCLink> tableOfContents;

    @JsonProperty
    public List<TOCLink> pageList;
    @JsonIgnore
    //public List<Link> landmarks;
    public Link[] landmarks;
    @JsonIgnore
    //public List<Link> LOI;
    public Link[] LOI;
    @JsonIgnore
    //public List<Link> LOA;
    public Link[] LOA;
    @JsonIgnore
    //public List<Link> LOV;
    public Link[] LOV;
    @JsonIgnore
    //public List<Link> LOT;
    public Link[] LOT;

    public HashMap<String, String> internalData;

    @JsonIgnore
    //public List<Link> otherLinks;
    public Link[] otherLinks;

    @JsonProperty("cover")
    public Link coverLink;

    public EpubPublication() {
        this.matchingLinks = new ArrayList<>();
        this.links = new ArrayList<>();
        this.spines = new ArrayList<>();
        this.encryptions = new ArrayList<>();
        this.resources = new ArrayList<>();
        this.guides = new ArrayList<>();
        this.internalData = new HashMap<>();

        this.linkMap = new HashMap<>();
    }

    @Override
    public String toString() {
        return "EpubPublication{" +
                "metadata=" + metadata +
                ", tableOfContents=" + tableOfContents +
                ", linkMap=" + linkMap +
                ", links=" + links +
                ", matchingLinks=" + matchingLinks +
                ", spines=" + spines +
                ", encryptions=" + encryptions +
                ", resources=" + resources +
                ", guides=" + guides +
                ", pageList=" + pageList +
                ", landmarks=" + Arrays.toString(landmarks) +
                ", LOI=" + Arrays.toString(LOI) +
                ", LOA=" + Arrays.toString(LOA) +
                ", LOV=" + Arrays.toString(LOV) +
                ", LOT=" + Arrays.toString(LOT) +
                ", internalData=" + internalData +
                ", otherLinks=" + Arrays.toString(otherLinks) +
                ", coverLink=" + coverLink +
                '}';
    }

    public Link getResourceMimeType(String resourcePath) {
        if (linkMap.containsKey(resourcePath)) {
            return linkMap.get(resourcePath);
        }
        return null;
    }
}