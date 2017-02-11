package com.codetoart.r2_streamer.server.handler;

import android.os.Bundle;
import android.util.Log;

import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.model.publication.EpubPublication;
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

public class SpineHandler extends DefaultHandler {
    private static final String CONTAINER_DATA = "Container";
    private static final String PUBLICATION_DATA = "Publication";
    private final String TAG = "SpineHandler";

    private Container container;
    private EpubPublication publication;
    private Bundle bundle;
    private Response response;

    public SpineHandler() {
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
        try {
            Method method = session.getMethod();
            String uri = session.getUri();
            Log.d(TAG, "Method: " + method + ", Url: " + uri);

            this.bundle = uriResource.initParameter(Bundle.class);
            container = (Container) bundle.get(CONTAINER_DATA);
            publication = (EpubPublication) bundle.get(PUBLICATION_DATA);

            if (uri.endsWith("/spineHandler")) {
                JSONArray spineArray = new JSONArray();
                for (Link link : publication.spine) {
                    JSONObject spineObject = new JSONObject();
                    spineObject.put("href", link.getHref());
                    spineObject.put("typeLink", link.getTypeLink());
                    spineArray.put(spineObject);
                }
                response = NanoHTTPD.newFixedLengthResponse(spineArray.toString());
            }
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }
    }
}