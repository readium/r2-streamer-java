package org.readium.sample;

/**
 * @author gautam chibde on 8/6/17.
 */

public interface Constant {
    int PORT_NUMBER = 3000;

    String EPUB_TITLE = "SmokeTestFXL.epub";

    String BASE_URL = "http://127.0.0.1";
    String URL = BASE_URL + ":" + PORT_NUMBER + "/" + EPUB_TITLE;
    String MANIFEST = "/manifest";
}
