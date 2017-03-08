package com.codetoart.r2_streamer.model.publication.subject;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class Subject implements Parcelable{
    public String name;
    public String sortAs;
    public String scheme;
    public String code;

    public Subject() {
    }

    public Subject(String name) {
        this.name = name;
    }

    protected Subject(Parcel in) {
        name = in.readString();
        sortAs = in.readString();
        scheme = in.readString();
        code = in.readString();
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(sortAs);
        parcel.writeString(scheme);
        parcel.writeString(code);
    }
}