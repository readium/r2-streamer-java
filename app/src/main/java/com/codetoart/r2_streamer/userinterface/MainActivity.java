package com.codetoart.r2_streamer.userinterface;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.codetoart.r2_streamer.R;
import com.codetoart.r2_streamer.model.container.DirectoryContainer;
import com.codetoart.r2_streamer.server.EpubServer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
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
        /*EpubContainer container = new EpubContainer(path + "/Download/demoFile.epub");
        container.rawData("chapter21.html");            //instead hardcoding path, use webview url path

        int size = container.rawDataSize();
        Log.d(TAG, "Epub File Size: " + size);*/

        DirectoryContainer dc = new DirectoryContainer(path + "/Download/demoTest/");
        dc.rawData("chapter10.html");
    }

    public void test(View view){

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
