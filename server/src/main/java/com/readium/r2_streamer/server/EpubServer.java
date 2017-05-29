package com.readium.r2_streamer.server;

import com.readium.r2_streamer.fetcher.EpubFetcher;
import com.readium.r2_streamer.fetcher.EpubFetcherException;
import com.readium.r2_streamer.model.container.Container;
import com.readium.r2_streamer.model.publication.EpubPublication;
import com.readium.r2_streamer.model.publication.link.Link;
import com.readium.r2_streamer.parser.EpubParser;
import com.readium.r2_streamer.server.handler.ManifestHandler;
import com.readium.r2_streamer.server.handler.MediaOverlayHandler;
import com.readium.r2_streamer.server.handler.ResourceHandler;
import com.readium.r2_streamer.server.handler.SearchQueryHandler;

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

    public void addEpub(Container container, String filePath) {
        try {
            EpubPublication publication = parse(container);

            addLinks(publication);

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
            e.printStackTrace();
        }
    }

    /**
     * Adds links to the publication
     * <p>
     * ref=> https://github.com/readium/webpub-manifest#links
     *
     * @param publication publication with parsed OPF data
     */
    private void addLinks(EpubPublication publication) {
        containsMediaOverlay = false;
        for (Link link : publication.links) {
            if (link.rel.equals("media-overlay")) {
                System.out.println("media-overlay" + link.rel);
                containsMediaOverlay = true;
                link.href = link.href.replace("port", "localhost" + getListeningPort());
            }
        }

        // A manifest must contain at least one link using the self relationship.
        // This link must point to the canonical location of the manifest using an absolute URI:
        publication.links.add(new Link(
                "localhost/" + getListeningPort() + MANIFEST_HANDLE,
                "self",
                "application/webpub+json"));

        publication.links.add(new Link(
                "localhost/" + getListeningPort() + SEARCH_QUERY_HANDLE,
                "search",
                "text/html"));
    }

    private EpubPublication parse(Container container) {
        EpubParser parser = new EpubParser(container);
        return parser.parseEpubFile();
    }
}