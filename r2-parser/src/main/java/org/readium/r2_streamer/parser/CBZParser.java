package org.readium.r2_streamer.parser;

import org.readium.r2_streamer.model.container.Container;
import org.readium.r2_streamer.model.publication.EpubPublication;
import org.readium.r2_streamer.model.publication.link.Link;

/**
 * Class to handle parsing of the .cbz (Comic book archive)
 * ref => https://en.wikipedia.org/wiki/Comic_book_archive
 *
 * @author gautam chibde on 5/6/17.
 */

public class CBZParser {
    private static final String TAG = CBZParser.class.getSimpleName();

    /**
     * function converts all the images inside the .cbz file into
     * link and addes them to spine and linkMap
     *
     * @param container   contains implementation for getting raw data from file.
     * @param publication The `Publication` object resulting from the parsing.
     */
    public static void parseCBZ(Container container, EpubPublication publication) {

        publication.internalData.put("type", "cbz");
        // since all the image files are inside zip rootpath is kept empty
        publication.internalData.put("rootfile", "");

        for (String name : container.listFiles()) {
            Link link = new Link();
            link.typeLink = getMediaType(name);
            link.href = name;
            // Add the book images to the spine element
            publication.spines.add(link);
            // Add to the resource linkMap for ResourceHandler to publish on the server
            publication.linkMap.put(name, link);
        }
    }

    /**
     * Returns the mimetype depending on the file format
     *
     * @param name file name
     * @return mimetype of the input file
     */
    private static String getMediaType(String name) {
        if (name.contains(".jpg") || name.contains("jpeg")) {
            return "image/jpeg";
        } else if (name.contains("png")) {
            return "image/png";
        } else {
            return "";
        }
    }
}
