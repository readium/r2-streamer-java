package com.codetoart.r2_streamer.server;

import com.codetoart.r2_streamer.model.container.EpubContainer;
import com.codetoart.r2_streamer.server.handler.EpubHandler;

import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by Shrikant on 20-Jan-17.
 */

public class EpubServer extends RouterNanoHTTPD {

    public EpubServer() {
        super(8080);
    }

    public void addEpub(String containerPath, EpubContainer epubContainer) {
        addRoute(containerPath, EpubHandler.class);
    }
}
