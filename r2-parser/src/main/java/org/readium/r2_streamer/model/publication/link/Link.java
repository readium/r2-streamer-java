package org.readium.r2_streamer.model.publication.link;

import org.readium.r2_streamer.model.publication.SMIL.MediaOverlays;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class Link implements Serializable {
    private static final long serialVersionUID = 7612342295622776147L;
    public String id;
    public String originalHref;
    public String href;
    public List<String> rel = new ArrayList<>();
    public String typeLink;
    public int height;
    public int width;
    public String bookTitle;
    public String chapterTitle;
    public String type;
    public List<String> properties;
    public String duration;
    public boolean templated;
    public MediaOverlays mediaOverlay;

    public Link() {
        this.properties = new ArrayList<>();
        this.mediaOverlay = new MediaOverlays();
    }

    public Link(String href, String rel, String typeLink) {
        this.href = href;
        this.rel.add(rel);
        this.typeLink = typeLink;
    }

    @Override
    public String toString() {
        return "Link{" +
                "id='" + id + '\'' +
                ", originalHref='" + originalHref + '\'' +
                ", href='" + href + '\'' +
                ", mediaOverlay=" + mediaOverlay +
                ", rel=" + rel +
                ", typeLink='" + typeLink + '\'' +
                ", height=" + height +
                ", width=" + width +
                ", bookTitle='" + bookTitle + '\'' +
                ", chapterTitle='" + chapterTitle + '\'' +
                ", type='" + type + '\'' +
                ", properties=" + properties +
                ", duration='" + duration + '\'' +
                ", templated=" + templated +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalHref() {
        return originalHref;
    }

    public void setOriginalHref(String originalHref) {
        this.originalHref = originalHref;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isTemplated() {
        return templated;
    }

    public void setTemplated(boolean templated) {
        this.templated = templated;
    }
}