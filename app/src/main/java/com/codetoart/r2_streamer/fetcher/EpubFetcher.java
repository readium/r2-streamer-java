package com.codetoart.r2_streamer.fetcher;

import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.model.publication.EpubPublication;

/**
 * Created by Shrikant on 27-Jan-17.
 */

public class EpubFetcher implements Fetcher {
    private Container container;
    private EpubPublication publication;
    private String rootFileDirectory;

    public EpubFetcher(Container container, EpubPublication publication) throws EpubFetcherException {
        this.container = container;
        this.publication = publication;

        String rootPath = publication.internalData.get("rootfile");
        if (rootPath != null) {
            this.rootFileDirectory = rootPath;
        } else {
            throw new EpubFetcherException("No rootFile in internalData, unable to get path to publication");
        }
    }

    @Override
    public String getData(String path) throws EpubFetcherException {
        return null;
    }

    @Override
    public String getDataSize(String path) throws EpubFetcherException {
        return null;
    }
}
