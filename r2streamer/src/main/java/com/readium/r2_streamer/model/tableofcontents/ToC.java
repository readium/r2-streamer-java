package com.readium.r2_streamer.model.tableofcontents;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 27-Feb-17.
 */

public class ToC implements Parcelable {
    public String docTitle;
    public List<TOCLink> tocLinks;

    public ToC() {
        tocLinks = new ArrayList<>();
    }

    public ToC(String docTitle, List<TOCLink> navPoint) {
        this.docTitle = docTitle;
        this.tocLinks = navPoint;
    }

    protected ToC(Parcel in) {
        docTitle = in.readString();
        tocLinks = in.createTypedArrayList(TOCLink.CREATOR);
    }

    public static final Creator<ToC> CREATOR = new Creator<ToC>() {
        @Override
        public ToC createFromParcel(Parcel in) {
            return new ToC(in);
        }

        @Override
        public ToC[] newArray(int size) {
            return new ToC[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(docTitle);
        parcel.writeTypedList(tocLinks);
    }
}
