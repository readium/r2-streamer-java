package com.codetoart.r2_streamer.server;

import com.codetoart.r2_streamer.fetcher.EpubFetcher;
import com.codetoart.r2_streamer.fetcher.EpubFetcherException;
import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.model.publication.EpubPublication;
import com.codetoart.r2_streamer.parser.EpubParser;
import com.codetoart.r2_streamer.server.handler.EpubHandler;
import com.codetoart.r2_streamer.server.handler.ManifestItemHandler;
import com.codetoart.r2_streamer.server.handler.SearchQueryHandler;

import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by Shrikant Badwaik on 20-Jan-17.
 */

public class EpubServer extends RouterNanoHTTPD {
    private static final String SPINE_HANDLE = "/spines";
    private static final String TOC_HANDLE = "/table_of_contents";
    private static final String MANIFEST_ITEM_HANDLE = "/(.*)";
    private static final String SEARCH_QUERY_HANDLE = "/query=(.*)";

    public EpubServer() {
        super(8080);
    }

    public void addEpub(Container container, String filePath) {
        try {
            EpubPublication publication = parse(container);
            EpubFetcher fetcher = new EpubFetcher(container, publication);

            addRoute(filePath + SPINE_HANDLE, EpubHandler.class, fetcher);
            addRoute(filePath + TOC_HANDLE, EpubHandler.class, fetcher);
            addRoute(filePath + SEARCH_QUERY_HANDLE, SearchQueryHandler.class, fetcher);
            addRoute(filePath + MANIFEST_ITEM_HANDLE, ManifestItemHandler.class, fetcher);
        } catch (EpubFetcherException e) {
            e.printStackTrace();
        }
    }

    private EpubPublication parse(Container container) {
        EpubParser parser = new EpubParser(container);
        return parser.parseEpubFile();
    }
}