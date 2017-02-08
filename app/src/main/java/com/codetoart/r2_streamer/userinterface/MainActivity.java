package com.codetoart.r2_streamer.userinterface;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.codetoart.r2_streamer.R;
import com.codetoart.r2_streamer.model.container.EpubContainer;
import com.codetoart.r2_streamer.parser.EpubParserException;
import com.codetoart.r2_streamer.server.EpubServer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private EpubServer mEpubServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mEpubServer = new EpubServer();
            mEpubServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Server is running. Point your browser at http://localhost:8080/");
    }

    public void unzip(View view) throws IOException {
        String path = Environment.getExternalStorageDirectory().getPath();
        EpubContainer container = new EpubContainer(path + "/Download/demoFile.epub");
        container.rawData("chapter21.html");            //instead hardcoding path, use webview url path

        int size = container.rawDataSize("chapter21.html");
        Log.d(TAG, "Epub File Size: " + size);

        /*DirectoryContainer dc = new DirectoryContainer(path + "/Download/demoTest/");
        dc.rawData("chapter10.html");
        int size = dc.rawDataSize("chapter10.html");
        Log.d(TAG, "File Size: " + size);*/
    }

    public void test(View view) throws IOException, EpubParserException {
        String path = Environment.getExternalStorageDirectory().getPath();
        EpubContainer ec = new EpubContainer(path + "/Download/georgiacfi.epub");
        mEpubServer.addEpub(ec, path + "/Download/georgiacfi.epub");

        /*EpubParser p = new EpubParser(new EpubContainer(Environment.getExternalStorageDirectory().getPath() + "/Download/internallinks.epub"));
        p.parseEpubFile();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEpubServer != null && mEpubServer.isAlive()) {
            mEpubServer.stop();
        }

        Log.d(TAG, "Server has been stopped");
    }
}