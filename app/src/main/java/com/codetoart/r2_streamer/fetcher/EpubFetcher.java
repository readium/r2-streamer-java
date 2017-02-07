package com.codetoart.r2_streamer.fetcher;

import android.util.Log;

import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.model.publication.EpubPublication;
import com.codetoart.r2_streamer.model.publication.Link;
import com.codetoart.r2_streamer.streams.seekableinputstream.SeekableInputStream;

/**
 * Created by Shrikant Badwaik on 27-Jan-17.
 */

public class EpubFetcher implements Fetcher {
    private final String TAG = "EpubFetcher";
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
        Link assetLink = publication.getResourceLink(path);
        if (assetLink == null) {
            Log.e(TAG, path + " file is missing");
            throw new EpubFetcherException(path + " file is missing");
        }
        String epubPath = rootFileDirectory.concat(path);
        String containerData = container.rawData(epubPath);
        if (containerData == null) {
            Log.e(TAG, epubPath + " file is missing");
            throw new EpubFetcherException(epubPath + " file is missing");
        }
        return containerData;
    }

    @Override
    public int getDataSize(String path) throws EpubFetcherException {
        Link assetLink = publication.getResourceLink(path);
        if (assetLink == null) {
            Log.e(TAG, path + " file is missing");
            throw new EpubFetcherException(path + " file is missing");
        }
        String epubPath = rootFileDirectory.concat(path);
        int dataSize = container.rawDataSize(epubPath);
        if (dataSize == 0) {
            Log.e(TAG, epubPath + "file is missing");
            throw new EpubFetcherException(epubPath + "file is missing");
        }
        return dataSize;
    }

    @Override
    public SeekableInputStream getDataStream(String path) throws EpubFetcherException {
        Link assetLink = publication.getResourceLink(path);
        if (assetLink == null) {
            Log.e(TAG, path + " file is missing");
            throw new EpubFetcherException(path + " file is missing");
        }
        String epubPath = rootFileDirectory.concat(path);
        SeekableInputStream inputStream = container.rawDataInputStream(epubPath);
        if (inputStream == null) {
            Log.e(TAG, epubPath + "file is missing");
            throw new EpubFetcherException(epubPath + "file is missing");
        }
        return inputStream;
    }
}