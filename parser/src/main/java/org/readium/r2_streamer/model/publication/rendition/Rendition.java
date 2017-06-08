package org.readium.r2_streamer.model.publication.rendition;

import java.io.Serializable;

/**
 * Created by Shrikant Badwaik on 25-Jan-17.
 */

public class Rendition implements Serializable {
    private static final long serialVersionUID = 7426472295421776147L;
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

    @Override
    public String toString() {
        return "Rendition{" +
                "layout=" + layout +
                ", flow=" + flow +
                ", orientation=" + orientation +
                ", spread=" + spread +
                ", viewport='" + viewport + '\'' +
                '}';
    }

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
}