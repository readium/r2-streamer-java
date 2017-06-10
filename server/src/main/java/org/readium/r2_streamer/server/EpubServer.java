package org.readium.r2_streamer.server;

import org.readium.r2_streamer.fetcher.EpubFetcher;
import org.readium.r2_streamer.fetcher.EpubFetcherException;
import org.readium.r2_streamer.model.container.Container;
import org.readium.r2_streamer.model.publication.EpubPublication;
import org.readium.r2_streamer.model.publication.link.Link;
import org.readium.r2_streamer.parser.EpubParser;
import org.readium.r2_streamer.server.handler.ManifestHandler;
import org.readium.r2_streamer.server.handler.MediaOverlayHandler;
import org.readium.r2_streamer.server.handler.ResourceHandler;
import org.readium.r2_streamer.server.handler.SearchQueryHandler;

import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by Shrikant Badwaik on 20-Jan-17.
 */

public class EpubServer extends RouterNanoHTTPD {
    private static final String SEARCH_QUERY_HANDLE = "/search";
    private static final String MANIFEST_HANDLE = "/manifest";
    private static final String MANIFEST_ITEM_HANDLE = "/(.*)";
    private static final String MEDIA_OVERLAY_HANDLE = "/media-overlay";
    private boolean containsMediaOverlay = false;

    public EpubServer(int portNo) {
        super(portNo);
    }


    /**
     * Creates local server routes for manifest,search and media-overlay
     *
     * @param container contains implementation for getting raw data from file
     * @param filePath  path to the epub/cbz file
     */
    public void addEpub(Container container, String filePath) {
        try {
            EpubPublication publication = parse(container, filePath);

            addLinks(publication, filePath);

            EpubFetcher fetcher = new EpubFetcher(container, publication);
            if (containsMediaOverlay) {
                addRoute(filePath + MEDIA_OVERLAY_HANDLE, MediaOverlayHandler.class, fetcher);
            }
            //addRoute(filePath + SPINE_HANDLE, EpubHandler.class, fetcher);
            //addRoute(filePath + TOC_HANDLE, EpubHandler.class, fetcher);
            addRoute(filePath + MANIFEST_HANDLE, ManifestHandler.class, fetcher);
            addRoute(filePath + SEARCH_QUERY_HANDLE, SearchQueryHandler.class, fetcher);
            addRoute(filePath + MANIFEST_ITEM_HANDLE, ResourceHandler.class, fetcher);
        } catch (EpubFetcherException e) {
            System.out.println("EpubServer" + " EpubFetcherException: " + e);
        }
    }

    /**
     * Adds links to the publication
     * <p>
     * ref=> https://github.org/readium/webpub-manifest#links
     *
     * @param publication publication with parsed OPF data
     * @param filePath
     */
    private void addLinks(EpubPublication publication, String filePath) {
        containsMediaOverlay = false;
        for (Link link : publication.links) {
            if (link.rel.contains("media-overlay")) {
                containsMediaOverlay = true;
                link.href = link.href.replace("port", "localhost:" + getListeningPort() + filePath);
            }
        }

        // A manifest must contain at least one link using the self relationship.
        // This link must point to the canonical location of the manifest using an absolute URI:
        publication.links.add(new Link(
                "localhost:" + getListeningPort() + filePath + MANIFEST_HANDLE,
                "self",
                "application/webpub+json"));

        publication.links.add(new Link(
                "localhost:" + getListeningPort() + filePath + SEARCH_QUERY_HANDLE,
                "search",
                "text/html"));
    }

    private EpubPublication parse(Container container, String filePath) {
        EpubParser parser = new EpubParser(container);
        return parser.parseEpubFile(filePath);
    }
}