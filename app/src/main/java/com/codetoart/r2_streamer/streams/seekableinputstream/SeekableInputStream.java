package com.codetoart.r2_streamer.streams.seekableinputstream;

import java.io.IOException;

/**
 * Created by Shrikant Badwaik on 01-Feb-17.
 */

public class SeekableInputStream implements Seekable {

    public int length;
    public int offset;

    public SeekableInputStream() {
        super();
    }

    public SeekableInputStream(int length, int offset) {
        this.length = length;
        this.offset = offset;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getOffset() throws IOException {
        return offset;
    }

    @Override
    public boolean isBytesAvailable() {
        return true;
    }

    @Override
    public void openFile() {
    }

    @Override
    public boolean getBufferStatus(int[] buffer, int availableData) {
        return false;
    }

    public int readFile(int[] buffer, int length) {
        byte[] byteArray = new byte[length];        //length = maximum bytes to read
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = byteArray[i];
        }
        return byteArray.length;        //no of bytes read
    }

    @Override
    public void seek(int offset, SeekWhence whence) {
    }

    @Override
    public void closeFile() {
    }
}