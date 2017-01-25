package com.codetoart.r2_streamer.server.handler;

import com.codetoart.r2_streamer.model.container.EpubContainer;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

/**
 * Created by Shrikant on 24-Jan-17.
 */

public class EpubHandler extends DefaultHandler {

    @Override
    public String getText() {
        return "Hello from the dark side!!!!!";
    }

    @Override
    public String getMimeType() {
        return "text/plain";
    }

    @Override
    public IStatus getStatus() {
        return Status.OK;
    }

    @Override
    public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return newFixedLengthResponse(getText());
    }
}
