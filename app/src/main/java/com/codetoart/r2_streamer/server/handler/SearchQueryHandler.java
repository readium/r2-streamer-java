package com.codetoart.r2_streamer.server.handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.codetoart.r2_streamer.fetcher.EpubFetcher;
import com.codetoart.r2_streamer.fetcher.EpubFetcherException;
import com.codetoart.r2_streamer.model.publication.Link;
import com.codetoart.r2_streamer.model.searcher.SearchResult;
import com.codetoart.r2_streamer.server.ResponseStatus;
import com.codetoart.r2_streamer.util.Constants;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

/**
 * Created by Shrikant Badwaik on 17-Feb-17.
 */

public class SearchQueryHandler extends RouterNanoHTTPD.DefaultHandler {
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
        return "text/plain";
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

            int startIndex = uri.indexOf("=");
            String searchQueryPath = uri.substring(startIndex + 1);

            JSONArray searchArray = new JSONArray();
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

                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(searchResult);
                    Log.d(TAG, "JSON : " + json);

                    JSONObject searchObject = new JSONObject();
                    searchObject.put(Constants.JSON_STRING, json);
                    searchArray.put(searchObject);
                }
            }
            response = NanoHTTPD.newFixedLengthResponse(searchArray.toString());
        } catch (EpubFetcherException | JSONException | JsonGenerationException | JsonMappingException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        } catch (IOException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }
        return response;
    }

    @Nullable
    private String parseChapterTitle(String searchData) {
        Document document = Jsoup.parse(searchData);
        Element h1Element = document.select("h1").first();
        if (h1Element != null) {
            return h1Element.text();
        }

        /*try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(searchData));
            int eventType = parser.getEventType();
            String startTagName = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        startTagName = parser.getName();
                        break;
                    case XmlPullParser.TEXT:
                        if (startTagName != null && startTagName.equals("h1"))
                            return parser.getText();
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }*/

        /*try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(searchData)));
            document.getDocumentElement().normalize();
            if (document != null) {
                Element headingElement = (Element) document.getDocumentElement().getElementsByTagName("h1").item(0);
                if (headingElement != null) {
                    return headingElement.getTextContent();
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }*/

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