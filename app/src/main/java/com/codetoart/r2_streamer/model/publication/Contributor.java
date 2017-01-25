package com.codetoart.r2_streamer.model.publication;

/**
 * Created by Shrikant on 25-Jan-17.
 */

public class Contributor {
    private String name;
    private String sortAs;
    private String identifier;
    private String role;

    public Contributor() {
    }

    public Contributor(String name, String sortAs, String identifier, String role) {
        this.name = name;
        this.sortAs = sortAs;
        this.identifier = identifier;
        this.role = role;
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

    public void setmSortAs(String sortAs) {
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
