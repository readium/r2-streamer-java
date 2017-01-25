package com.codetoart.r2_streamer.model.publication.rendition;

/**
 * Created by Shrikant on 25-Jan-17.
 */

public class Rendition {
    private RenditionLayout layout;
    private RenditionFlow flow;
    private RenditionOrientation orientation;
    private RenditionSpread spread;
    private String viewport;

    public Rendition() {
    }

    public Rendition(RenditionLayout layout, RenditionFlow flow, RenditionOrientation orientation, RenditionSpread spread, String viewport) {
        this.layout = layout;
        this.flow = flow;
        this.orientation = orientation;
        this.spread = spread;
        this.viewport = viewport;
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
