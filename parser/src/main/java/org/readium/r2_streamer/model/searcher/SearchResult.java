package org.readium.r2_streamer.model.searcher;

/**
 * Created by Shrikant Badwaik on 17-Feb-17.
 */

public class SearchResult {
    private int searchIndex;
    private String resource;
    private String title;
    private String searchQuery;
    private String matchString;
    private String textBefore;
    private String textAfter;

    public SearchResult() {
    }

    public SearchResult(int searchIndex, String resource, String title, String searchQuery, String matchString, String textBefore, String textAfter) {
        this.searchIndex = searchIndex;
        this.resource = resource;
        this.title = title;
        this.searchQuery = searchQuery;
        this.matchString = matchString;
        this.textBefore = textBefore;
        this.textAfter = textAfter;
    }

    public int getSearchIndex() {
        return searchIndex;
    }

    public void setSearchIndex(int searchIndex) {
        this.searchIndex = searchIndex;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getMatchString() {
        return matchString;
    }

    public void setMatchString(String matchString) {
        this.matchString = matchString;
    }

    public String getTextBefore() {
        return textBefore;
    }

    public void setTextBefore(String textBefore) {
        this.textBefore = textBefore;
    }

    public String getTextAfter() {
        return textAfter;
    }

    public void setTextAfter(String textAfter) {
        this.textAfter = textAfter;
    }
}