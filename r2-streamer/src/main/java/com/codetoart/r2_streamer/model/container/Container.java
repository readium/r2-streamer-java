package com.codetoart.r2_streamer.model.container;

import java.io.InputStream;

/**
 * Created by Shrikant Badwaik on 24-Jan-17.
 */

public interface Container {
    String rawData(String relativePath) throws NullPointerException;

    int rawDataSize(String relativePath);

    InputStream rawDataInputStream(String relativePath) throws NullPointerException;
}
