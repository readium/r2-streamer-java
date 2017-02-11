package com.codetoart.r2_streamer.server;

import android.os.Bundle;

import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.model.publication.EpubPublication;
import com.codetoart.r2_streamer.parser.EpubParser;
import com.codetoart.r2_streamer.server.handler.HtmlHandler;
import com.codetoart.r2_streamer.server.handler.SpineHandler;

import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by Shrikant Badwaik on 20-Jan-17.
 */

public class EpubServer extends RouterNanoHTTPD {
    private static final String CONTAINER_DATA = "Container";
    private static final String PUBLICATION_DATA = "Publication";

    public EpubServer() {
        super(8080);
    }

    public void addEpub(Container container, String filePath) {
        EpubPublication publication = parse(container);

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONTAINER_DATA, container);
        bundle.putSerializable(PUBLICATION_DATA, publication);

        addRoute(filePath + "/spineHandler", SpineHandler.class, bundle);
        addRoute(filePath + "/(.*)", HtmlHandler.class, bundle);
    }

    private EpubPublication parse(Container container) {
        EpubParser parser = new EpubParser(container);
        return parser.parseEpubFile();
    }
}