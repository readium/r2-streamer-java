package com.codetoart.r2_streamer.model.publication;

import android.support.annotation.Nullable;

import com.codetoart.r2_streamer.model.publication.link.Link;
import com.codetoart.r2_streamer.model.publication.metadata.MetaData;
import com.codetoart.r2_streamer.model.tableofcontents.NCX;
import com.codetoart.r2_streamer.model.tableofcontents.TOCLink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class EpubPublication {
    public MetaData metadata;
    public NCX tableOfContents;

    public Map<String,Link> linkMap;
    public List<Link> links;
    public List<Link> matchingLinks;
    public List<Link> spines;
    public List<Link> resources;
    public List<Link> guides;

    //public List<Link> pageList;
    public Link[] pageList;
    //public List<Link> landmarks;
    public Link[] landmarks;
    //public List<Link> LOI;
    public Link[] LOI;
    //public List<Link> LOA;
    public Link[] LOA;
    //public List<Link> LOV;
    public Link[] LOV;
    //public List<Link> LOT;
    public Link[] LOT;

    public Map<String, String> internalData;

    //public List<Link> otherLinks;
    public Link[] otherLinks;

    public Link coverLink;

    public EpubPublication() {
        this.matchingLinks = new ArrayList<>();
        this.links = new ArrayList<>();
        this.spines = new ArrayList<>();
        this.resources = new ArrayList<>();
        this.guides= new ArrayList<>();
        this.internalData = new HashMap<>();

        this.linkMap = new HashMap<>();
    }

    public EpubPublication(MetaData metadata, List<Link> links, List<Link> matchingLinks, List<Link> spines, List<Link> resources,List<Link> guides, NCX tableOfContents, Link[] pageList, Link[] landmarks, Link[] LOI, Link[] LOA, Link[] LOV, Link[] LOT, HashMap<String, String> internalData, Link[] otherLinks, Link coverLink) {
        this.metadata = metadata;
        this.links = links;
        this.matchingLinks = matchingLinks;
        this.spines = spines;
        this.resources = resources;
        this.guides = guides;
        this.tableOfContents = tableOfContents;
        this.pageList = pageList;
        this.landmarks = landmarks;
        this.LOI = LOI;
        this.LOA = LOA;
        this.LOV = LOV;
        this.LOT = LOT;
        this.internalData = internalData;
        this.otherLinks = otherLinks;
        this.coverLink = coverLink;
    }

    public Link getCoverLink() {
        return getLink("cover");
    }

    @Nullable
    private Link getLink(String coverPath) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).rel.equals(coverPath)) {
                return links.get(i);
            }
        }
        return null;
    }

    public Link getResourceMimeType(String resourcePath) {
        /*for (int i = 0; i < links.size(); i++) {
            if (links.get(i).href.equals(resourcePath)) {
                return links.get(i);
            }
        }*/

        if(linkMap.containsKey(resourcePath)){
            return linkMap.get(resourcePath);
        }

        return null;
    }
}