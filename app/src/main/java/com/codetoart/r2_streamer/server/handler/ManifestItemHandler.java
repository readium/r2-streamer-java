package com.codetoart.r2_streamer.server.handler;

import android.util.Log;

import com.codetoart.r2_streamer.fetcher.EpubFetcher;
import com.codetoart.r2_streamer.fetcher.EpubFetcherException;
import com.codetoart.r2_streamer.model.publication.Link;
import com.codetoart.r2_streamer.server.ResponseStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by Shrikant Badwaik on 10-Feb-17.
 */

public class ManifestItemHandler extends RouterNanoHTTPD.DefaultHandler {
    private static final String TAG = "ManifestItemHandler";
    private Response response;

    public ManifestItemHandler() {
    }

    @Override
    public String getMimeType() {
        return null;
    }

    @Override
    public String getText() {
        return ResponseStatus.FAILURE_RESPONSE;
    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return Status.NOT_ACCEPTABLE;
    }

    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        NanoHTTPD.Method method = session.getMethod();
        String uri = session.getUri();
        Log.d(TAG, "Method: " + method + ", Url: " + uri);

        try {
            EpubFetcher fetcher = uriResource.initParameter(EpubFetcher.class);

            int offset = uri.indexOf("/", 0);
            int startIndex = uri.indexOf("/", offset + 1);
            String filePath = uri.substring(startIndex + 1);
            Link link = fetcher.publication.getResourceMimeType(filePath);
            String mimeType = link.getTypeLink();

            InputStream inputStream = fetcher.getDataInputStream(filePath);
            response = NanoHTTPD.newFixedLengthResponse(Status.OK, mimeType, inputStream, inputStream.available());
        } catch (EpubFetcherException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        } catch (IOException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }
        return response;
    }
}