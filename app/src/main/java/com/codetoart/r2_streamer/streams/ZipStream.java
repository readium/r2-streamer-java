package com.codetoart.r2_streamer.streams;

import com.codetoart.r2_streamer.streams.seekableinputstream.SeekableInputStream;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Shrikant Badwaik on 01-Feb-17.
 */

public class ZipStream extends SeekableInputStream {
    public ZipFile zipFile;
    public String filePath;
    public Status fileStatus = Status.NOT_OPEN;
    public boolean bytesAvailable;
    public int offset;
    public int length;
    //private ZipEntry zipEntry;

    public ZipStream(String zipFilePath, String path) throws IOException {        //zipFilePath = epubFilePath, path = relativePath
        this.zipFile = new ZipFile(zipFilePath);
        //ZipEntry zipEntry = zipFile.getEntry(filePath);
        if (zipFile != null) {
            this.length = zipFile.size();
        }
        this.filePath = path;
    }

    /*@Override
    public int getOffset() {
        *//*Enumeration<? extends ZipEntry> e = zipFile.entries();
        while (e.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) e.nextElement();

        }*//*
        return offset;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public boolean isBytesAvailable() {
        return (offset < length);
    }

    @Override
    public void openFile() {
        zipEntry = zipFile.getEntry(filePath);
        if (zipEntry != null) {
            this.fileStatus = Status.valueOfEnum("Opened");
        } else {
            this.fileStatus = Status.valueOfEnum("Error");
        }
    }

    @Override
    public boolean getBufferStatus(int[] buffer, int availableData) {
        return false;
    }

    @Override
    public int readFile(int[] buffer, int length) {
        return super.readFile(buffer, length);
    }

    @Override
    public void seek(int offset, SeekWhence whence) {

    }

    @Override
    public void closeFile() {
        try {
            zipFile.close();
            this.fileStatus = Status.valueOfEnum("Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}