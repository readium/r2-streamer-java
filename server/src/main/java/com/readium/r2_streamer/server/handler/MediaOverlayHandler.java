package com.readium.r2_streamer.server.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readium.r2_streamer.fetcher.EpubFetcher;
import com.readium.r2_streamer.model.publication.SMIL.Clip;
import com.readium.r2_streamer.model.publication.SMIL.MediaOverlayNode;
import com.readium.r2_streamer.model.publication.link.Link;
import com.readium.r2_streamer.server.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by gautam chibde on 25/5/17.
 */

public class MediaOverlayHandler extends RouterNanoHTTPD.DefaultHandler {
    public static final String TAG = MediaOverlayNode.class.getSimpleName();

    @Override
    public String getText() {
        return ResponseStatus.FAILURE_RESPONSE;
    }

    @Override
    public String getMimeType() {
        return "application/webpub+json";
    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.OK;
    }

    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        EpubFetcher fetcher = uriResource.initParameter(EpubFetcher.class);

        if (session.getParameters().containsKey("query")) {
            String searchQueryPath = session.getParameters().get("query").get(0);
            HashMap<String, Link> linkMap = fetcher.publication.linkMap;

            for (String key : linkMap.keySet()) {
                Link link = linkMap.get(key);
                List<Clip> clips = new ArrayList<>();
                if (key.contains(searchQueryPath)) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        for (MediaOverlayNode mediaOverlayNode : link.mediaOverlay.mediaOverlayNodes) {
                            if (mediaOverlayNode.text.contains(searchQueryPath)) {
                                for (MediaOverlayNode node : mediaOverlayNode.children) {
                                    clips.add(node.clip());
                                }
                                break;
                            }
                        }
                        String json = objectMapper.writeValueAsString(clips);
                        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), json);
                    } catch (JsonProcessingException e) {
                        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), ResponseStatus.FAILURE_RESPONSE);
                    }
                }
            }
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        } else {
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }
    }
}
