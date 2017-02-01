package com.codetoart.r2_streamer.fetcher;

/**
 * Created by Shrikant on 30-Jan-17.
 */

public interface Fetcher {
    String getData(String path) throws EpubFetcherException;
    String getDataSize(String path) throws EpubFetcherException;
}
