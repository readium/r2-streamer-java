package org.readium.r2_streamer.fetcher;

import org.readium.r2_streamer.model.container.Container;
import org.readium.r2_streamer.model.publication.EpubPublication;

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
        String data = container.rawData(path);
        if (data == null) {
            System.out.println(TAG + " file is missing " + path);
            throw new EpubFetcherException(path + " file is missing");
        }
        return data;
    }

    @Override
    public int getDataSize(String path) throws EpubFetcherException {
        int dataSize = container.rawDataSize(path);
        if (dataSize == 0) {
            System.out.println(TAG + " file is missing " + path);
            throw new EpubFetcherException(path + "file is missing");
        }
        return dataSize;
    }

    @Override
    public InputStream getDataInputStream(String path) throws EpubFetcherException {
        InputStream dataInputStream = container.rawDataInputStream(path);
        if (dataInputStream == null) {
            System.out.println(TAG + " file is missing " + path);
            throw new EpubFetcherException(path + "file is missing");
        }
        return dataInputStream;
    }
}