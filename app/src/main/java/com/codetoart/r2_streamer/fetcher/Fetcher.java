package com.codetoart.r2_streamer.fetcher;

import com.codetoart.r2_streamer.streams.seekableinputstream.SeekableInputStream;

/**
 * Created by Shrikant Badwaik on 30-Jan-17.
 */

public interface Fetcher {
    String getData(String path) throws EpubFetcherException;

    int getDataSize(String path) throws EpubFetcherException;

    SeekableInputStream getDataStream(String path) throws EpubFetcherException;
}
