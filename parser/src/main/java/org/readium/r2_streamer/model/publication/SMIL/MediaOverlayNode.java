package org.readium.r2_streamer.model.publication.SMIL;

import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gautam chibde on 23/5/17.
 */

public class MediaOverlayNode implements Serializable {
    private static final long serialVersionUID = 7329984331545950872L;

    public String text;
    public String audio;
    public List<String> role;
    public List<MediaOverlayNode> children;

    public MediaOverlayNode() {
        this.children = new ArrayList<>();
        this.role = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "MediaOverlayNode{" +
                "text='" + text + '\'' +
                ", audio='" + audio + '\'' +
                ", role=" + role +
                ", children=" + children +
                '}';
    }

    /**
     * Generate Clip from current instance object
     *
     * @return The generated Clip.
     */
    public Clip clip() throws IndexOutOfBoundsException {
        Clip newClip = new Clip();

        // Retrieve the audioString (containing timers + audiofile url), then
        // retrieve both.
        newClip.relativeUrl = this.audio.split("#")[0];
        String times = this.audio.split("#")[1];
        return parseTimer(times, newClip);
    }

    /**
     * Parse the time String to fill clip.
     *
     * @param times The time string ("t=S.MS,S.MS") as created in {@link SMILParser#parseAudio(Element)}
     * @param clip  The Clip instance where to fill the parsed data.
     * @return returns clips with start, end and duration
     */
    private Clip parseTimer(String times, Clip clip) throws IndexOutOfBoundsException {
        // Remove "t=" prefix from times string.
        times = times.substring(2, times.length());
        // Parse start and end times.
        Double start = Double.parseDouble(times.split(",")[0]);
        Double end = Double.parseDouble(times.split(",")[1]);
        clip.start = start;
        clip.end = end;
        clip.duration = end - start;
        return clip;
    }
}
