package com.readium.r2_streamer.server;

import com.readium.r2_streamer.fetcher.EpubFetcher;
import com.readium.r2_streamer.fetcher.EpubFetcherException;
import com.readium.r2_streamer.model.container.Container;
import com.readium.r2_streamer.model.publication.Encryption;
import com.readium.r2_streamer.model.publication.EpubPublication;
import com.readium.r2_streamer.parser.Decoder;
import com.readium.r2_streamer.parser.EpubParser;
import com.readium.r2_streamer.server.handler.ManifestHandler;
import com.readium.r2_streamer.server.handler.ResourceHandler;
import com.readium.r2_streamer.server.handler.SearchQueryHandler;

import java.io.InputStream;

import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by Shrikant Badwaik on 20-Jan-17.
 */

public class EpubServer extends RouterNanoHTTPD {
    private static final String TAG = EpubServer.class.getName();
    private static final String SEARCH_QUERY_HANDLE = "/search";
    private static final String MANIFEST_HANDLE = "/manifest";
    private static final String MANIFEST_ITEM_HANDLE = "/(.*)";

    public EpubServer(int portNo) {
        super(portNo);
    }

    public void addEpub(Container container, String filePath) {
        try {
            EpubPublication publication = parse(container);
            EpubFetcher fetcher = new EpubFetcher(container, publication);

            //addRoute(filePath + SPINE_HANDLE, EpubHandler.class, fetcher);
            //addRoute(filePath + TOC_HANDLE, EpubHandler.class, fetcher);
            addRoute(filePath + MANIFEST_HANDLE, ManifestHandler.class, fetcher);
            addRoute(filePath + SEARCH_QUERY_HANDLE, SearchQueryHandler.class, fetcher);
            addRoute(filePath + MANIFEST_ITEM_HANDLE, ResourceHandler.class, fetcher);
        } catch (EpubFetcherException e) {
            e.printStackTrace();
        }
    }

    private EpubPublication parse(Container container) {
        EpubParser parser = new EpubParser(container);
        return parser.parseEpubFile();
    }
}