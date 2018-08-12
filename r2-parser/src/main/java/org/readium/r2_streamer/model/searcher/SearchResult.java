package org.readium.r2_streamer.model.searcher;

/**
 * Created by Shrikant Badwaik on 17-Feb-17.
 */

public class SearchResult {
    private int searchIndex;
    private String href;
    private String originalHref;
    private String title;
    private String searchQuery;
    private String matchString;
    private String textBefore;
    private String textAfter;

    public SearchResult() {
    }

    public SearchResult(int searchIndex, String href, String originalHref, String title,
                        String searchQuery, String matchString, String textBefore,
                        String textAfter) {
        this.searchIndex = searchIndex;
        this.href = href;
        this.originalHref = originalHref;
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

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getOriginalHref() {
        return originalHref;
    }

    public void setOriginalHref(String originalHref) {
        this.originalHref = originalHref;
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