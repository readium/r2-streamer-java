package com.codetoart.r2_streamer.server.handler;

import android.os.Bundle;
import android.util.Log;

import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.model.publication.EpubPublication;
import com.codetoart.r2_streamer.server.ResponseStatus;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by Shrikant Badwaik on 10-Feb-17.
 */

public class xHtmlHandler extends RouterNanoHTTPD.DefaultHandler {
    private static final String CONTAINER_DATA = "Container";
    private static final String PUBLICATION_DATA = "Publication";
    private final String TAG = "HtmlHandler";

    private Container container;
    private EpubPublication publication;
    private Bundle bundle;
    private NanoHTTPD.Response response;

    public xHtmlHandler() {
    }

    @Override
    public String getText() {
        return ResponseStatus.FAILURE_RESPONSE;
    }

    @Override
    public String getMimeType() {
        return "application/xhtml+xml";
    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.NOT_ACCEPTABLE;
    }

    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        NanoHTTPD.Method method = session.getMethod();
        String uri = session.getUri();
        Log.d(TAG, "Method: " + method + ", Url: " + uri);

        this.bundle = uriResource.initParameter(Bundle.class);
        container = (Container) bundle.get(CONTAINER_DATA);
        publication = (EpubPublication) bundle.get(PUBLICATION_DATA);

        int offset = uri.indexOf(".epub/", 0);
        int startIndex = offset + 6;
        String filePath = uri.substring(startIndex);
        String data = container.rawData(filePath);

        response = NanoHTTPD.newFixedLengthResponse(data);
        if (response != null) {
            return response;
        } else {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }
    }
}
