package com.codetoart.r2_streamer.server.handler;

import android.util.Log;

import com.codetoart.r2_streamer.fetcher.EpubFetcher;
import com.codetoart.r2_streamer.model.publication.Link;
import com.codetoart.r2_streamer.server.ResponseStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String SPINE_HANDLE = "/spineHandle";
    private static final String TAG = "EpubHandler";
    private Response response;

    public EpubHandler() {
    }

    @Override
    public String getText() {
        return ResponseStatus.FAILURE_RESPONSE;
    }

    @Override
    public String getMimeType() {
        return "application/json";
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

        try {
            EpubFetcher fetcher = uriResource.initParameter(EpubFetcher.class);

            if (uri.endsWith(SPINE_HANDLE)) {
                JSONArray spineArray = new JSONArray();
                for (Link link : fetcher.publication.spines) {
                    JSONObject spineObject = new JSONObject();
                    if (link.getTypeLink().equals("application/xhtml+xml")) {
                        spineObject.put("href", link.getHref());
                        spineObject.put("typeLink", link.getTypeLink());
                        spineArray.put(spineObject);
                    }
                }
                response = NanoHTTPD.newFixedLengthResponse(spineArray.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }
        return response;
    }
}