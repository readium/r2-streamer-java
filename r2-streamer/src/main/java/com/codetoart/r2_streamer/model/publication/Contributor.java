package com.codetoart.r2_streamer.model.publication;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class Contributor {
    public String name;
    public String sortAs;
    public String identifier;
    public String role;

    public Contributor() {
    }

    public Contributor(String name) {
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
