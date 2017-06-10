package org.readium.r2_streamer.model.container;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Shrikant Badwaik on 24-Jan-17.
 */

public interface Container {
    String rawData(String relativePath) throws NullPointerException;

    int rawDataSize(String relativePath);

    List<String> listFiles();

    InputStream rawDataInputStream(String relativePath) throws NullPointerException;
}
