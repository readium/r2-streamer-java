package com.codetoart.r2_streamer.model.container;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Shrikant on 24-Jan-17.
 */

public class DirectoryContainer implements Container {
    private static String TAG = "DirectoryContainer";
    private String mRootPath;

    public DirectoryContainer(String rootPath) {
        this.mRootPath = rootPath;
        File epubDirectoryFile = new File(mRootPath);
        if (!epubDirectoryFile.exists()) {
            Log.e(TAG, "No such directory exists at path: " + epubDirectoryFile);
            return;
        }

        Log.d(TAG, "Directory exists at path: " + epubDirectoryFile);
    }

    @Override
    public String rawData(String relativePath) throws NullPointerException {
        String filePath = mRootPath.concat(relativePath);
        File epubFile = new File(filePath);

        if (epubFile != null && epubFile.exists()) {
            Log.d(TAG, relativePath + " File exists at given path");

            try {
                InputStream is = new FileInputStream(epubFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                Log.d(TAG, sb.toString());

                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!epubFile.exists()) {
            Log.e(TAG, relativePath + " No such file exists at given path");
        }
        return null;
    }

    @Override
    public int rawDataSize() {
        return 0;
    }
}
