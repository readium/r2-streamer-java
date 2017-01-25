package com.codetoart.r2_streamer.model.publication;

import java.util.Date;

/**
 * Created by Shrikant on 25-Jan-17.
 */

public class Link {
    private String href;
    private String[] rel;
    private String typeLink;
    private int height;
    private int width;
    private String title;
    private String[] properties;
    private Date duration;
    private boolean templated;

    public Link() {
    }

    public Link(String href, String[] rel, String typeLink, int height, int width, String title, String[] properties, Date duration, boolean templated) {
        this.href = href;
        this.rel = rel;
        this.typeLink = typeLink;
        this.height = height;
        this.width = width;
        this.title = title;
        this.properties = properties;
        this.duration = duration;
        this.templated = templated;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String[] getRel() {
        return rel;
    }

    public void setRel(String[] rel) {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getProperties() {
        return properties;
    }

    public void setProperties(String[] properties) {
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
}