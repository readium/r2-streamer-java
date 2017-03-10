package com.codetoart.r2_streamer.server.handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codetoart.r2_streamer.fetcher.EpubFetcher;
import com.codetoart.r2_streamer.fetcher.EpubFetcherException;
import com.codetoart.r2_streamer.model.publication.link.Link;
import com.codetoart.r2_streamer.model.searcher.SearchQueryResults;
import com.codetoart.r2_streamer.model.searcher.SearchResult;
import com.codetoart.r2_streamer.server.ResponseStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

/**
 * Created by Shrikant Badwaik on 17-Feb-17.
 */

public class SearchQueryHandler extends DefaultHandler {
    private static final String TAG = "SearchQueryHandler";
    private Response response;

    public SearchQueryHandler() {
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

            String queryParameter = session.getQueryParameterString();
            int startIndex = queryParameter.indexOf("=");
            String searchQueryPath = queryParameter.substring(startIndex + 1);

            //JSONArray searchArray = new JSONArray();
            SearchQueryResults searchQueryResults = new SearchQueryResults();
            for (Link link : fetcher.publication.spines) {
                String searchData = fetcher.getData(link.getHref());
                Pattern pattern = Pattern.compile(searchQueryPath);
                Matcher matcher = pattern.matcher(searchData);
                while (matcher.find()) {
                    int start = matcher.start();
                    String prev = searchData.substring(start - 20, start);
                    String next = searchData.substring(start + searchQueryPath.length(), start + 20);
                    String match = prev.concat(searchQueryPath).concat(next);
                    String matchString = removeHtmlTags(match);

                    SearchResult searchResult = new SearchResult();
                    searchResult.setSearchIndex(start);
                    searchResult.setResource(link.getHref());
                    searchResult.setMatchString(matchString);
                    searchResult.setTextBefore(prev);
                    searchResult.setTextAfter(next);

                    String title = parseChapterTitle(searchData);
                    if (title != null) {
                        searchResult.setTitle(title);
                    } else {
                        searchResult.setTitle("Title not available");
                    }
                    searchQueryResults.searchResultList.add(searchResult);

                    /*ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(searchResult);
                    Log.d(TAG, "SEARCH_JSON : " + json);

                    JSONObject searchObject = new JSONObject();
                    searchObject.put(JSON_STRING, json);
                    searchArray.put(searchObject);*/
                }
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(searchQueryResults);
            response = NanoHTTPD.newFixedLengthResponse(Status.OK, getMimeType(), json);
            //response = NanoHTTPD.newFixedLengthResponse(searchArray.toString());

            return response;
        } catch (EpubFetcherException | JsonProcessingException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        } /*catch (EpubFetcherException | JSONException | JsonGenerationException | JsonMappingException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        } catch (IOException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }*/
    }

    @Nullable
    private String parseChapterTitle(String searchData) {
        Document document = Jsoup.parse(searchData);
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

    @NonNull
    private String removeHtmlTags(String html) {
        html = html.replaceAll("<(.*?)\\>", " ");
        html = html.replaceAll("<(.*?)\\\n", " ");
        html = html.replaceFirst("(.*?)\\>", " ");
        html = html.replaceAll("<[^>]*", " ");
        html = html.replaceAll("<[^>]+>", " ");
        html = html.replaceAll("[^<]*>", " ");
        html = html.replaceAll("&nbsp;", " ");
        return html.replaceAll("&amp;", " ");
    }
}