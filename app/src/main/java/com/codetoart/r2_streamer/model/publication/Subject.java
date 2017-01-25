package com.codetoart.r2_streamer.model.publication;

/**
 * Created by Shrikant on 25-Jan-17.
 */

public class Subject {
    private String name;
    private String sortAs;
    private String scheme;
    private String code;

    public Subject() {
    }

    public Subject(String name, String sortAs, String scheme, String code) {
        this.name = name;
        this.sortAs = sortAs;
        this.scheme = scheme;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortAs() {
        return sortAs;
    }

    public void setSortAs(String sortAs) {
        this.sortAs = sortAs;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}