package com.readium.r2_streamer.model.publication.SMIL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gautam chibde on 23/5/17.
 */

public class MediaOverlays implements Serializable {

    private static final long serialVersionUID = 335418192699543070L;

    public List<MediaOverlayNode> mediaOverlayNodes;

    public MediaOverlays() {
        this.mediaOverlayNodes = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "MediaOverlays{" +
                "mediaOverlayNodes=" + mediaOverlayNodes +
                '}';
    }
}
