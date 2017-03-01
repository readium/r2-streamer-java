package com.codetoart.r2_streamer.fetcher;

import android.util.Log;

import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.model.publication.EpubPublication;
import com.codetoart.r2_streamer.model.publication.link.Link;

import java.io.InputStream;

/**
 * Created by Shrikant Badwaik on 27-Jan-17.
 */

public class EpubFetcher implements Fetcher {
    private final String TAG = "EpubFetcher";
    public Container container;
    public EpubPublication publication;
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
        Link assetLink = publication.getResourceMimeType(path);
        if (assetLink == null) {
            Log.e(TAG, path + " file is missing");
            throw new EpubFetcherException(path + " file is missing");
        }
        String data = container.rawData(path);
        if (data == null) {
            Log.e(TAG, path + " file is missing");
            throw new EpubFetcherException(path + " file is missing");
        }
        return data;
    }

    @Override
    public int getDataSize(String path) throws EpubFetcherException {
        Link assetLink = publication.getResourceMimeType(path);
        if (assetLink == null) {
            Log.e(TAG, path + " file is missing");
            throw new EpubFetcherException(path + " file is missing");
        }
        int dataSize = container.rawDataSize(path);
        if (dataSize == 0) {
            Log.e(TAG, path + "file is missing");
            throw new EpubFetcherException(path + "file is missing");
        }
        return dataSize;
    }

    @Override
    public InputStream getDataInputStream(String path) throws EpubFetcherException {
        Link assetLink = publication.getResourceMimeType(path);
        if (assetLink == null) {
            Log.e(TAG, path + " file is missing");
            throw new EpubFetcherException(path + " file is missing");
        }
        InputStream dataInputStream = container.rawDataInputStream(path);
        if (dataInputStream == null) {
            Log.e(TAG, path + "file is missing");
            throw new EpubFetcherException(path + "file is missing");
        }
        return dataInputStream;
    }
}