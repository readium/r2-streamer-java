package org.readium.r2_streamer.server.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.readium.r2_streamer.fetcher.EpubFetcher;
import org.readium.r2_streamer.model.publication.SMIL.MediaOverlayNode;
import org.readium.r2_streamer.model.publication.SMIL.MediaOverlays;
import org.readium.r2_streamer.model.publication.link.Link;
import org.readium.r2_streamer.server.ResponseStatus;

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

        if (session.getParameters().containsKey("resource")) {
            String searchQueryPath = session.getParameters().get("resource").get(0);
            List<Link> spines = fetcher.publication.spines;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String json = objectMapper.writeValueAsString(getMediaOverlay(spines, searchQueryPath));
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), json);
            } catch (JsonProcessingException e) {
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), ResponseStatus.FAILURE_RESPONSE);
            }
        } else {
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }
    }

    private MediaOverlays getMediaOverlay(List<Link> spines, String searchQueryPath) {
        for (Link link : spines) {
            if (link.href.contains(searchQueryPath)) {
                return link.mediaOverlay;
            }
        }
        return new MediaOverlays();
    }
}
