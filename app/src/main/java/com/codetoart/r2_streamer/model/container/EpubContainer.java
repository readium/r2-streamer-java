package com.codetoart.r2_streamer.model.container;

import android.util.Log;

import com.codetoart.r2_streamer.streams.ZipStream;
import com.codetoart.r2_streamer.streams.seekableinputstream.SeekableInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Shrikant Badwaik on 24-Jan-17.
 */

public class EpubContainer implements Container {
    private final String TAG = "EpubContainer";
    private String epubFilePath;
    private ZipFile zipFile;

    public EpubContainer(String epubFilePath) throws IOException {
        this.epubFilePath = epubFilePath;
        this.zipFile = new ZipFile(epubFilePath);

        Log.d(TAG, "Reading epub at path: " + epubFilePath);
    }

    @Override
    public String rawData(String relativePath) throws NullPointerException {
        Log.d(TAG, "Reading file at path: " + relativePath);

        try {
            ZipEntry zipEntry = zipFile.getEntry(relativePath);
            InputStream is = zipFile.getInputStream(zipEntry);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);        //.append('\n');
            }
            Log.d(TAG, "Reading data: " + sb.toString());

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int rawDataSize(String relativePath) {
        ZipEntry zipEntry = zipFile.getEntry(relativePath);
        return ((int) zipEntry.getSize());
    }

    @Override
    public SeekableInputStream rawDataInputStream(String relativePath) throws NullPointerException {
        try {
            SeekableInputStream inputStream = new ZipStream(epubFilePath, relativePath);
            if (inputStream != null) {
                return inputStream;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}