package org.readium.r2_streamer.model.publication.contributor;

import java.io.Serializable;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class Contributor implements Serializable {
    private static final long serialVersionUID = 7666462295622776147L;
    public String name;
    public String sortAs;
    public String identifier;
    public String role;

    public Contributor() {
    }

    @Override
    public String toString() {
        return "Contributor{" +
                "name='" + name + '\'' +
                ", sortAs='" + sortAs + '\'' +
                ", identifier='" + identifier + '\'' +
                ", role='" + role + '\'' +
                '}';
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