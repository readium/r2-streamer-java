package com.codetoart.r2_streamer.model.container;

/**
 * Created by Shrikant on 24-Jan-17.
 */

public interface Container {
    String rawData(String relativePath) throws NullPointerException;

    int rawDataSize();
}
