package com.codetoart.r2_streamer.model.publication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class Link {
    public String href;
    public List<String> rel;
    //public String[] rel;
    public String typeLink;
    public int height;
    public int width;
    public String title;
    public List<String> properties;
    //public String[] properties;
    public Date duration;
    public boolean templated;

    public Link() {
        this.rel = new ArrayList<String>();
        this.properties = new ArrayList<String>();
    }

    public Link(String href, String rel, String typeLink) {
        this.href = href;
        this.rel.add(rel);
        this.typeLink = typeLink;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}