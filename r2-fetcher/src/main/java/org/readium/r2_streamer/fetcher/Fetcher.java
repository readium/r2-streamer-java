package org.readium.r2_streamer.fetcher;

import java.io.InputStream;

/**
 * Created by Shrikant Badwaik on 30-Jan-17.
 */

public interface Fetcher {
    String getData(String path) throws EpubFetcherException;

    int getDataSize(String path) throws EpubFetcherException;

    InputStream getDataInputStream(String path) throws EpubFetcherException;
}
