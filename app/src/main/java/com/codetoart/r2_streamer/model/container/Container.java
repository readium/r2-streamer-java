package com.codetoart.r2_streamer.model.container;

import com.codetoart.r2_streamer.streams.seekableinputstream.SeekableInputStream;

import java.io.IOException;

/**
 * Created by Shrikant Badwaik on 24-Jan-17.
 */

public interface Container {
    String rawData(String relativePath) throws NullPointerException;

    int rawDataSize(String relativePath);

    SeekableInputStream rawDataInputStream(String relativePath) throws NullPointerException;
}
