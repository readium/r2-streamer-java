package org.readium.r2_streamer.model.publication.subject;

import java.io.Serializable;


/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class Subject implements Serializable{
    private static final long serialVersionUID = 7526472295622776147L;

    public String name;
    public String sortAs;
    public String scheme;
    public String code;

    public Subject() {
    }

    @Override
    public String toString() {
        return "Subject{" +
                "name='" + name + '\'' +
                ", sortAs='" + sortAs + '\'' +
                ", scheme='" + scheme + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    public Subject(String name) {
        this.name = name;
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