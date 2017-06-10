package org.readium.r2_streamer.model.tableofcontents;

import org.readium.r2_streamer.model.publication.link.Link;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 27-Feb-17.
 */

public class TOCLink extends Link implements Serializable{
    private static final long serialVersionUID = 752647222222776147L;
    public String sectionTitle;
    public String playOrder;
    public List<TOCLink> tocLinks;

    public TOCLink() {
        tocLinks = new ArrayList<>();
    }

    public TOCLink(String sectionTitle, String playOrder, ArrayList<TOCLink> navPoints) {
        this.sectionTitle = sectionTitle;
        this.playOrder = playOrder;
        this.tocLinks = navPoints;
    }

    @Override
    public String toString() {
        return "TOCLink{" +
                "sectionTitle='" + bookTitle + '\'' +
                ", tocLinks=" + tocLinks +
                '}';
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getPlayOrder() {
        return playOrder;
    }

    public void setPlayOrder(String playOrder) {
        this.playOrder = playOrder;
    }

    public List<TOCLink> getTocLinks() {
        return tocLinks;
    }

    public void setTocLinks(List<TOCLink> navPoints) {
        this.tocLinks = navPoints;
    }
}