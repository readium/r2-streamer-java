package com.codetoart.r2_streamer.server;

import android.util.Log;

import com.codetoart.r2_streamer.model.container.Container;
import com.codetoart.r2_streamer.server.handler.EpubHandler;

import fi.iki.elonen.router.RouterNanoHTTPDPooled;

/**
 * Created by Shrikant Badwaik on 20-Jan-17.
 */

public class EpubServer extends RouterNanoHTTPDPooled {
    private final String TAG = "EpubServer";

    public EpubServer() {
        super(8080);
    }

    public void addEpub(Container container, String filePath) {
        Log.d(TAG, filePath);
        addRoute(filePath, new EpubHandler(container, filePath));
    }
}