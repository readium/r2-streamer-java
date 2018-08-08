package org.readium.r2_streamer.server.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.readium.r2_streamer.fetcher.EpubFetcher;
import org.readium.r2_streamer.fetcher.EpubFetcherException;
import org.readium.r2_streamer.model.publication.link.Link;
import org.readium.r2_streamer.model.searcher.SearchQueryResults;
import org.readium.r2_streamer.model.searcher.SearchResult;
import org.readium.r2_streamer.server.ResponseStatus;

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

        //Log.d(TAG, "Method: " + method + ", Url: " + uri);

        try {
            EpubFetcher fetcher = uriResource.initParameter(EpubFetcher.class);

            String queryParameter = session.getQueryParameterString();
            int startIndex = queryParameter.indexOf("=");
            String searchQueryPath = queryParameter.substring(startIndex + 1);
            if (searchQueryPath.contains("%20")) {
                searchQueryPath = searchQueryPath.replaceAll("%20", " ");
            }
            Pattern pattern = Pattern.compile(searchQueryPath, Pattern.CASE_INSENSITIVE);

            SearchQueryResults searchQueryResults = new SearchQueryResults();

            for (Link link : fetcher.publication.spines) {

                String htmlText = fetcher.getData(link.getHref());
                String bodyText = Jsoup.parse(htmlText).body().text();
                Matcher matcher = pattern.matcher(bodyText);

                while (matcher.find()) {

                    int start = matcher.start();
                    String prev = getTextBefore(bodyText, matcher, 15);
                    String next = getTextAfter(bodyText, matcher, 15);
                    String match = prev.concat(searchQueryPath).concat(next);

                    SearchResult searchResult = new SearchResult();
                    searchResult.setSearchIndex(start);
                    searchResult.setResource(link.getHref());
                    searchResult.setSearchQuery(searchQueryPath);
                    searchResult.setMatchString(match);
                    searchResult.setTextBefore(prev);
                    searchResult.setTextAfter(next);
                    searchResult.setTitle(parseChapterTitle(htmlText));

                    searchQueryResults.searchResultList.add(searchResult);
                }
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(searchQueryResults);
            response = NanoHTTPD.newFixedLengthResponse(Status.OK, getMimeType(), json);
            return response;

        } catch (EpubFetcherException | JsonProcessingException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }
    }

    private String parseChapterTitle(String searchData) {

        Document document = Jsoup.parse(searchData);

        Element h1Element = document.select("h1").first();
        if (h1Element != null)
            return h1Element.text();

        Element h2Element = document.select("h2").first();
        if (h2Element != null)
            return h2Element.text();

        Element titleElement = document.select("title").first();
        if (titleElement != null)
            return titleElement.text();

        return null;
    }

    private String getTextBefore(String text, Matcher matcher, int noOfWords) {

        int noOfWordsFound = 0;
        int i = matcher.start() - 1;
        Boolean lastCharSpace = null;

        for (; i >= 0; i--) {

            if (Character.isSpaceChar(text.charAt(i))) {

                if (lastCharSpace != null)
                    noOfWordsFound++;
                lastCharSpace = true;

            } else {
                lastCharSpace = false;
            }

            if (noOfWordsFound == noOfWords) {
                i++;
                break;
            }
        }

        if (i < 0) {
            return text.substring(0, matcher.start());
        } else {
            return "..." + text.substring(i, matcher.start());
        }
    }

    private String getTextAfter(String text, Matcher matcher, int noOfWords) {

        int noOfWordsFound = 0;
        int i = matcher.end();
        Boolean lastCharSpace = null;

        for (; i < text.length(); i++) {

            if (Character.isSpaceChar(text.charAt(i))) {

                if (lastCharSpace != null)
                    noOfWordsFound++;
                lastCharSpace = true;

            } else {
                lastCharSpace = false;
            }

            if (noOfWordsFound == noOfWords)
                break;
        }

        if (i == text.length()) {
            return text.substring(matcher.end(), text.length());
        } else {
            return text.substring(matcher.end(), i) + "...";
        }
    }
}