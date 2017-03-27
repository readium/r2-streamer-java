package com.readium.r2_streamer.model.publication.contributor;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class Contributor implements Parcelable {
    public String name;
    public String sortAs;
    public String identifier;
    public String role;

    public Contributor() {
    }

    public Contributor(String name) {
        this.name = name;
    }

    protected Contributor(Parcel in) {
        name = in.readString();
        sortAs = in.readString();
        identifier = in.readString();
        role = in.readString();
    }

    public static final Creator<Contributor> CREATOR = new Creator<Contributor>() {
        @Override
        public Contributor createFromParcel(Parcel in) {
            return new Contributor(in);
        }

        @Override
        public Contributor[] newArray(int size) {
            return new Contributor[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(sortAs);
        parcel.writeString(identifier);
        parcel.writeString(role);
    }
}