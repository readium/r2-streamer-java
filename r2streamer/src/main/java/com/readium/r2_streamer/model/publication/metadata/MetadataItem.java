package com.readium.r2_streamer.model.publication.metadata;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class MetadataItem implements Parcelable{
    public String property;
    public String value;
    public List<MetadataItem> children;

    public MetadataItem() {
    }

    public MetadataItem(String property, String value, List<MetadataItem> children) {
        this.property = property;
        this.value = value;
        this.children = children;
    }

    protected MetadataItem(Parcel in) {
        property = in.readString();
        value = in.readString();
        children = in.createTypedArrayList(MetadataItem.CREATOR);
    }

    public static final Creator<MetadataItem> CREATOR = new Creator<MetadataItem>() {
        @Override
        public MetadataItem createFromParcel(Parcel in) {
            return new MetadataItem(in);
        }

        @Override
        public MetadataItem[] newArray(int size) {
            return new MetadataItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(property);
        parcel.writeString(value);
        parcel.writeTypedList(children);
    }
}