package com.codetoart.r2_streamer.model.publication.link;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class Link implements Parcelable{
    public String id;
    public String href;
    public List<String> rel;
    public String typeLink;
    public int height;
    public int width;
    public String bookTitle;
    public String chapterTitle;
    public String type;
    public List<String> properties;
    public Date duration;
    public boolean templated;

    public Link() {
        this.rel = new ArrayList<>();
        this.properties = new ArrayList<>();
    }

    public Link(String href, String rel, String typeLink) {
        this.href = href;
        this.rel.add(rel);
        this.typeLink = typeLink;
    }

    protected Link(Parcel in) {
        id = in.readString();
        href = in.readString();
        rel = in.createStringArrayList();
        typeLink = in.readString();
        height = in.readInt();
        width = in.readInt();
        bookTitle = in.readString();
        chapterTitle = in.readString();
        type = in.readString();
        properties = in.createStringArrayList();
        duration = (Date) in.readSerializable();
        templated = in.readByte() != 0;
    }

    public static final Creator<Link> CREATOR = new Creator<Link>() {
        @Override
        public Link createFromParcel(Parcel in) {
            return new Link(in);
        }

        @Override
        public Link[] newArray(int size) {
            return new Link[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<String> getRel() {
        return rel;
    }

    public void setRel(List<String> rel) {
        this.rel = rel;
    }

    public String getTypeLink() {
        return typeLink;
    }

    public void setTypeLink(String typeLink) {
        this.typeLink = typeLink;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public Date getDuration() {
        return duration;
    }

    public void setDuration(Date duration) {
        this.duration = duration;
    }

    public boolean isTemplated() {
        return templated;
    }

    public void setTemplated(boolean templated) {
        this.templated = templated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(href);
        parcel.writeStringList(rel);
        parcel.writeString(typeLink);
        parcel.writeInt(height);
        parcel.writeInt(width);
        parcel.writeString(bookTitle);
        parcel.writeString(chapterTitle);
        parcel.writeString(type);
        parcel.writeStringList(properties);
        parcel.writeSerializable(duration);
        parcel.writeByte((byte) (templated ? 1 : 0));
    }
}