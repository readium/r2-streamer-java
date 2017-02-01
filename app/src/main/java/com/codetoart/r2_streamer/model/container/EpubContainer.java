package com.codetoart.r2_streamer.model.container;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Shrikant on 24-Jan-17.
 */

public class EpubContainer implements Container {
    private static String TAG = "EpubContainer";
    private String epubFilePath;
    private ZipFile zipFile;

    public EpubContainer(String epubFilePath) throws IOException {
        this.epubFilePath = epubFilePath;
        zipFile = new ZipFile(epubFilePath);

        Log.d(TAG, "Reading epub: " + epubFilePath);
    }

    @Override
    public String rawData(String relativePath) throws NullPointerException {
        Log.d(TAG, "Reading file: " + relativePath);

        try {
            ZipEntry zipEntry = zipFile.getEntry(relativePath);
            InputStream is = zipFile.getInputStream(zipEntry);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);        //.append('\n');
            }
            Log.d(TAG, sb.toString());

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int rawDataSize() {
        return zipFile.size();
    }
}
