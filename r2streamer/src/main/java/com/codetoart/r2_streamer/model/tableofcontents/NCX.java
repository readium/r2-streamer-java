package com.codetoart.r2_streamer.model.tableofcontents;

import com.codetoart.r2_streamer.model.publication.link.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 27-Feb-17.
 */

public class NCX {
    public String docTitle;
    public List<TOCLink> tocLinks;

    public NCX() {
        tocLinks = new ArrayList<>();
    }

    public NCX(String docTitle, List<TOCLink> navPoints) {
        this.docTitle = docTitle;
        this.tocLinks = navPoints;
    }
    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public List<TOCLink> getTocLinks() {
        return tocLinks;
    }

    public void setTocLinks(List<TOCLink> tocLinks) {
        this.tocLinks = tocLinks;
    }
}
