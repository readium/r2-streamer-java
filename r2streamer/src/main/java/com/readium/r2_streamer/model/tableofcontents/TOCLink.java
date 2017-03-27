package com.readium.r2_streamer.model.tableofcontents;

import android.os.Parcel;
import android.os.Parcelable;

import com.readium.r2_streamer.model.publication.link.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 27-Feb-17.
 */

public class TOCLink extends Link implements Parcelable{
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

    protected TOCLink(Parcel in) {
        sectionTitle = in.readString();
        playOrder = in.readString();
        tocLinks = in.createTypedArrayList(TOCLink.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sectionTitle);
        parcel.writeString(playOrder);
        parcel.writeTypedList(tocLinks);
    }

    public static final Creator<TOCLink> CREATOR = new Creator<TOCLink>() {
        @Override
        public TOCLink createFromParcel(Parcel in) {
            return new TOCLink(in);
        }

        @Override
        public TOCLink[] newArray(int size) {
            return new TOCLink[size];
        }
    };
}