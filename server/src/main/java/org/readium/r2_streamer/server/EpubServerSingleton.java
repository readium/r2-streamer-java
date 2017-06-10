package org.readium.r2_streamer.server;

/**
 * Created by Shrikant Badwaik on 06-Mar-17.
 */

public class EpubServerSingleton {
    private static EpubServer epubServerInstance;

    public static EpubServer getEpubServerInstance() {
        if (epubServerInstance == null) {
            epubServerInstance = new EpubServer(8080);
        }
        return epubServerInstance;
    }

    public static EpubServer getEpubServerInstance(int portNumber) {
        if (epubServerInstance == null) {
            epubServerInstance = new EpubServer(portNumber);
        }
        return epubServerInstance;
    }

    public static void resetServerInstance() {
        epubServerInstance = null;
    }
}
