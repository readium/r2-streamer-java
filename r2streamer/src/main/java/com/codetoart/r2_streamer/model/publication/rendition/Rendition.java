package com.codetoart.r2_streamer.model.publication.rendition;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class Rendition implements Parcelable {
    public RenditionLayout layout;
    public RenditionFlow flow;
    public RenditionOrientation orientation;
    public RenditionSpread spread;
    public String viewport;

    public Rendition() {
    }

    public Rendition(RenditionLayout layout, RenditionFlow flow, RenditionOrientation orientation, RenditionSpread spread, String viewport) {
        this.layout = layout;
        this.flow = flow;
        this.orientation = orientation;
        this.spread = spread;
        this.viewport = viewport;
    }

    protected Rendition(Parcel in) {
        layout = RenditionLayout.valueOfEnum(in.readString());
        flow = RenditionFlow.valueOfEnum(in.readString());
        orientation = RenditionOrientation.valueOfEnum(in.readString());
        spread = RenditionSpread.valueOfEnum(in.readString());
        viewport = in.readString();
    }

    public static final Creator<Rendition> CREATOR = new Creator<Rendition>() {
        @Override
        public Rendition createFromParcel(Parcel in) {
            return new Rendition(in);
        }

        @Override
        public Rendition[] newArray(int size) {
            return new Rendition[size];
        }
    };

    public RenditionLayout getLayout() {
        return layout;
    }

    public void setLayout(RenditionLayout layout) {
        this.layout = layout;
    }

    public RenditionFlow getFlow() {
        return flow;
    }

    public void setFlow(RenditionFlow flow) {
        this.flow = flow;
    }

    public RenditionOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(RenditionOrientation orientation) {
        this.orientation = orientation;
    }

    public RenditionSpread getSpread() {
        return spread;
    }

    public void setSpread(RenditionSpread spread) {
        this.spread = spread;
    }

    public String getViewport() {
        return viewport;
    }

    public void setViewport(String viewport) {
        this.viewport = viewport;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(layout.name());
        parcel.writeString(flow.name());
        parcel.writeString(orientation.name());
        parcel.writeString(spread.name());
        parcel.writeString(viewport);
    }
}
