package com.codetoart.r2_streamer.server.handler;

import android.support.annotation.Nullable;
import android.util.Log;

import com.codetoart.r2_streamer.fetcher.EpubFetcher;
import com.codetoart.r2_streamer.fetcher.EpubFetcherException;
import com.codetoart.r2_streamer.model.publication.Link;
import com.codetoart.r2_streamer.server.ResponseStatus;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

import static com.codetoart.r2_streamer.util.Constants.JSON_STRING;

/**
 * Created by Shrikant Badwaik on 24-Jan-17.
 */

public class EpubHandler extends DefaultHandler {
    private static final String SPINE_HANDLE = "/spines";
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
                    if (link.getTypeLink().equals("application/xhtml+xml")) {

                        String bookTitle = getBookTitle(fetcher);
                        if (bookTitle != null) {
                            link.setBookTitle(bookTitle);
                        }

                        String chapterTitle = getChapterTitle(fetcher, link);
                        if (chapterTitle != null) {
                            link.setChapterTitle(chapterTitle);
                        } else {
                            link.setChapterTitle("Title not available");
                        }

                        ObjectMapper objectMapper = new ObjectMapper();
                        String json = objectMapper.writeValueAsString(link);
                        Log.d(TAG, "JSON : " + json);

                        JSONObject spineObject = new JSONObject();
                        spineObject.put(JSON_STRING, json);
                        //spineObject.put(HREF, link.getHref());
                        //spineObject.put(TYPE_LINK, link.getTypeLink());
                        spineArray.put(spineObject);
                    }
                }
                response = NanoHTTPD.newFixedLengthResponse(spineArray.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        } catch (JsonGenerationException | JsonMappingException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        } catch (IOException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        } catch (EpubFetcherException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }
        return response;
    }

    private String getBookTitle(EpubFetcher fetcher) {
        return fetcher.publication.metadata.getTitle();
    }

    @Nullable
    private String getChapterTitle(EpubFetcher fetcher, Link link) throws EpubFetcherException {
        String spineData = fetcher.getData(link.getHref());
        Document document = Jsoup.parse(spineData);
        Element h1Element = document.select("h1").first();
        if (h1Element != null) {
            return h1Element.text();
        } else {
            Element h2Element = document.select("h2").first();
            if (h2Element != null) {
                return h2Element.text();
            } else {
                Element titleElement = document.select("title").first();
                if (titleElement != null) {
                    return titleElement.text();
                }
            }
        }
        return null;
    }
}