package com.codetoart.r2_streamer.server.handler;

import android.os.Bundle;
import android.util.Log;

import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.model.publication.EpubPublication;
import com.codetoart.r2_streamer.server.ResponseStatus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by Shrikant Badwaik on 10-Feb-17.
 */

public class HtmlHandler extends RouterNanoHTTPD.DefaultHandler {
    private static final String CONTAINER_DATA = "Container";
    private static final String PUBLICATION_DATA = "Publication";
    private final String TAG = "HtmlHandler";

    private Container container;
    private EpubPublication publication;
    private Bundle bundle;
    private NanoHTTPD.Response response;

    public HtmlHandler() {
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
        //try {
            NanoHTTPD.Method method = session.getMethod();
            String uri = session.getUri();
            Log.d(TAG, "Method: " + method + ", Url: " + uri);

            this.bundle = uriResource.initParameter(Bundle.class);
            container = (Container) bundle.get(CONTAINER_DATA);
            publication = (EpubPublication) bundle.get(PUBLICATION_DATA);

            String data = container.rawData(uri);

            /*InputStream is = new FileInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }*/

            response = NanoHTTPD.newFixedLengthResponse(data);

            return response;
        /*} catch (FileNotFoundException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        } catch (IOException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }*/
        //return null;
    }
}
