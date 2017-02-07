package com.codetoart.r2_streamer.server.handler;

import android.util.Log;

import com.codetoart.r2_streamer.fetcher.EpubFetcher;
import com.codetoart.r2_streamer.fetcher.EpubFetcherException;
import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.model.publication.EpubPublication;
import com.codetoart.r2_streamer.parser.EpubParser;
import com.codetoart.r2_streamer.server.ResponseStatus;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

/**
 * Created by Shrikant Badwaik on 24-Jan-17.
 */

public class EpubHandler extends DefaultHandler {
    private final String TAG = "EpubHandler";

    private Map<String, Container> containers;
    private Map<String, EpubPublication> publications;
    private Container container;
    private EpubPublication publication;
    private EpubParser parser;
    private String endPoint;

    public EpubHandler() {
    }

    public EpubHandler(Container container, String endPoint) {
        this.container = container;
        this.endPoint = endPoint;

        parser = new EpubParser(container);
        publication = new EpubPublication();
    }

    @Override
    public String getText() {
        return ResponseStatus.FAILURE_RESPONSE;
    }

    @Override
    public String getMimeType() {
        return "text/plain";
    }

    @Override
    public IStatus getStatus() {
        return Status.NOT_ACCEPTABLE;
    }

    @Override
    public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        Log.d(TAG, "Method: " + method + ", Url: " + uri);

        publication = parser.parseEpubFile();

        if (publication != null) {
            try {


                //containers.put(endPoint, container);
                //publications.put(endPoint, publication);

                EpubFetcher fetcher = new EpubFetcher(container, publication);
            } catch (EpubFetcherException e) {
                e.printStackTrace();
            }
        }
        return NanoHTTPD.newFixedLengthResponse(Status.OK, getMimeType(), ResponseStatus.SUCCESS_RESPONSE);
    }
}