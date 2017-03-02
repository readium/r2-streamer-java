package com.codetoart.r2_streamer.model.tableofcontents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 27-Feb-17.
 */

public class NCX {
    public String docTitle;
    public List<TOCLink> navPoint;

    public NCX() {
        navPoint = new ArrayList<>();
    }

    public NCX(String docTitle, List<TOCLink> navPoint) {
        this.docTitle = docTitle;
        this.navPoint = navPoint;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public List<TOCLink> getNavPoint() {
        return navPoint;
    }

    public void setNavPoint(List<TOCLink> navPoint) {
        this.navPoint = navPoint;
    }
}
