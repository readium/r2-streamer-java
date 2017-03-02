package com.codetoart.r2_streamer.model.tableofcontents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 27-Feb-17.
 */

public class ToC {
    public String docTitle;
    public List<TOCLink> tocLinks;

    public ToC() {
        tocLinks = new ArrayList<>();
    }

    public ToC(String docTitle, List<TOCLink> navPoint) {
        this.docTitle = docTitle;
        this.tocLinks = navPoint;
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

    public void setTocLinks(List<TOCLink> navPoint) {
        this.tocLinks = navPoint;
    }
}
