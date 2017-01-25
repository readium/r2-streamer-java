package com.codetoart.r2_streamer.model.publication;

import java.util.List;

/**
 * Created by Shrikant on 25-Jan-17.
 */

public class EpubPublication {
    private MetaData metadata;

    //private List<Link> links;
    private Link[] links;

    //private List<Link> spine;
    private Link[] spine;

    //private List<Link> resources;
    private Link[] resources;

    //private List<Link> TOC;
    private Link[] TOC;
    //private List<Link> pageList;
    private Link[] pageList;
    //private List<Link> landmarks;
    private Link[] landmarks;
    //private List<Link> LOI;
    private Link[] LOI;
    //private List<Link> LOA;
    private Link[] LOA;
    //private List<Link> LOV;
    private Link[] LOV;
    //private List<Link> LOT;
    private Link[] LOT;

    //private List<Link> otherLinks;
    private Link[] otherLinks;

    private Link coverLink;

    public EpubPublication() {
    }

    public EpubPublication(MetaData metadata, Link[] links, Link[] spine, Link[] resources, Link[] TOC, Link[] pageList, Link[] landmarks, Link[] LOI, Link[] LOA, Link[] LOV, Link[] LOT, Link[] otherLinks, Link coverLink) {
        this.metadata = metadata;
        this.links = links;
        this.spine = spine;
        this.resources = resources;
        this.TOC = TOC;
        this.pageList = pageList;
        this.landmarks = landmarks;
        this.LOI = LOI;
        this.LOA = LOA;
        this.LOV = LOV;
        this.LOT = LOT;
        this.otherLinks = otherLinks;
        this.coverLink = coverLink;
    }

    /*public Link getCoverLink() {
        return getLinkWithCover("cover");
    }

    public Link getLinkWithCover(String rel){
        String matchingLink = links.
    }

    public Link getResourceLink(String resourcePath){

    }*/

}