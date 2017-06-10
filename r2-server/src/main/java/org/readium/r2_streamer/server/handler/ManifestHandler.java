package org.readium.r2_streamer.server.handler;

import org.readium.r2_streamer.fetcher.EpubFetcher;
import org.readium.r2_streamer.server.ResponseStatus;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by mahavir on 3/2/17.
 */

public class ManifestHandler extends RouterNanoHTTPD.DefaultHandler {
    private static final String TAG = "ManifestHandler";

    @Override
    public String getMimeType() {
        return "application/webpub+json";
    }

    @Override
    public String getText() {
        return ResponseStatus.FAILURE_RESPONSE;
    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.OK;
    }

    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        try {

            EpubFetcher fetcher = uriResource.initParameter(EpubFetcher.class);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(fetcher.publication);

            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), json);

        } catch (JsonGenerationException | JsonMappingException e) {
            System.out.println(TAG + " JsonGenerationException | JsonMappingException " + e.toString());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        } catch (IOException e) {
            System.out.println(TAG + " IOException " + e.toString());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }
    }
}