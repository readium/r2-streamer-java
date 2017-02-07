package com.codetoart.r2_streamer.streams.seekableinputstream;

import java.io.IOException;

/**
 * Created by Shrikant Badwaik on 01-Feb-17.
 */

public interface Seekable {
    int length = 0;
    int offset = 0;

    int getLength();

    int getOffset() throws IOException;

    boolean isBytesAvailable();

    void openFile();

    boolean getBufferStatus(int[] buffer, int availableData);

    int readFile(int[] buffer, int length);

    void seek(int offset, SeekWhence whence);

    void closeFile();
}
