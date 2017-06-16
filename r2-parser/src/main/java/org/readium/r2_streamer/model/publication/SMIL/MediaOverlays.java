package org.readium.r2_streamer.model.publication.SMIL;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gautam chibde on 23/5/17.
 */

public class MediaOverlays implements Serializable {

    private static final long serialVersionUID = 335418192699543070L;

    @JsonProperty("media-overlay")
    public List<MediaOverlayNode> mediaOverlayNodes;

    public MediaOverlays() {
        this.mediaOverlayNodes = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "MediaOverlays{" +
                ", mediaOverlayNodes=" + mediaOverlayNodes +
                '}';
    }

    /**
     * Function return the path of the audio file for the
     * given page href
     *
     * @param href page href
     * @return audio file path for given SMIL
     */
    public String getAudioPath(String href) {
        // extract file name
        if (href.contains("/")) {
            int startIndex = href.lastIndexOf("/");
            href = href.substring(startIndex + 1, href.length());
        }
        String path = findAudioPath(href, this.mediaOverlayNodes);
        if (path != null) {
            return path;
        }
        return null;
    }

    /**
     * [RECURSIVE]
     * <p>
     * Return the file path from the first node element
     *
     * @param href  href of page
     * @param nodes media overlay nodes
     * @return audio file path
     */
    private String findAudioPath(String href,
                                 List<MediaOverlayNode> nodes) {
        // For each node of the current scope..
        for (MediaOverlayNode node : nodes) {
            if (node.audio != null) {
                if (node.text.contains(href)) {
                    if (node.audio.contains("#")) {
                        return node.audio.split("#")[0];
                    }
                }
            }
            if (node.role.contains("section")) {
                return findAudioPath(href, node.children);
            }
        }
        return null;
    }

    /**
     * <p>
     * Get the audio `Clip` associated to an audio Fragment id.
     * The fragment id can be found in the HTML document in <p> & <span> tags,
     * it refer to a element of one of the SMIL files, providing information
     * This function returns the clip representing this element from SMIL.
     * about the synchronized audio.
     * </p>
     *
     * @param forFragmentId The audio fragment id.
     * @return The `Clip`, representation of the associated SMIL element.
     */
    public Clip clip(String forFragmentId) {
        MediaOverlayNode node = findNode(forFragmentId, this.mediaOverlayNodes);
        if (node != null) {
            return node.clip();
        }
        return new Clip();
    }

    /**
     * [RECURSIVE]
     * <p>
     * Find the node (<par>) corresponding to "fragment" ?? nil.
     * </p>
     *
     * @param fragment The current fragment name for which we are looking the
     * @param nodes    The set of MediaOverlayNodes where to search. Default to  self children.
     * @return node corresponding to the fragment id, null if not found
     */
    private MediaOverlayNode findNode(String fragment,
                                      List<MediaOverlayNode> nodes) {
        // For each node of the current scope..
        for (MediaOverlayNode node : nodes) {
            if (node.text.contains(fragment)) {
                return node;
            }
            if (node.role.contains("section")) {
                return findNode(fragment, node.children);
            }
        }
        return null;
    }
}
