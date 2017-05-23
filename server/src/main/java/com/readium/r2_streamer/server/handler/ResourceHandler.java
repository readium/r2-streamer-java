package com.readium.r2_streamer.server.handler;

import com.readium.r2_streamer.fetcher.EpubFetcher;
import com.readium.r2_streamer.fetcher.EpubFetcherException;
import com.readium.r2_streamer.model.container.Container;
import com.readium.r2_streamer.model.container.EpubContainer;
import com.readium.r2_streamer.model.publication.Encryption;
import com.readium.r2_streamer.model.publication.link.Link;
import com.readium.r2_streamer.parser.Decoder;
import com.readium.r2_streamer.server.ResponseStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;

/**
 * Created by Shrikant Badwaik on 10-Feb-17.
 */

public class ResourceHandler extends DefaultHandler {
    private static final String TAG = "ResourceHandler";

    public ResourceHandler() {
    }

    @Override
    public String getMimeType() {
        return null;
    }

    private final String[] fonts = {".woff", ".ttf", ".obf", ".woff2", ".eot"};

    @Override
    public String getText() {
        return ResponseStatus.FAILURE_RESPONSE;
    }

    @Override
    public IStatus getStatus() {
        return Status.OK;
    }

    @Override
    public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        System.out.println(TAG + " Method: " + method + ", Url: " + uri);
        Response response;
        try {
            EpubFetcher fetcher = uriResource.initParameter(EpubFetcher.class);
            int offset = uri.indexOf("/", 0);
            int startIndex = uri.indexOf("/", offset + 1);
            String filePath = uri.substring(startIndex + 1);
            Link link = fetcher.publication.getResourceMimeType(filePath);
            String mimeType = link.getTypeLink();
            if (mimeType.equals("application/xhtml+xml")) {
                return serveResponse(session, fetcher.getDataInputStream(filePath), mimeType);
            }
            InputStream inputStream = null;
            if (isFontFile(filePath)) {
                Encryption encryption = Encryption.getEncryptionFormFontFilePath(
                        filePath,
                        fetcher.publication.encryptions);
                if (encryption != null) {
                    inputStream = Decoder.decode(
                            fetcher.publication.metadata.identifier,
                            fetcher.getDataInputStream(encryption.getProfile()),
                            encryption);
                }
            } else {
                inputStream = fetcher.getDataInputStream(filePath);
            }
            response = serveResponse(session, inputStream, mimeType);
        } catch (EpubFetcherException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(), ResponseStatus.FAILURE_RESPONSE);
        }
        return response;
    }

    private Response serveResponse(IHTTPSession session, InputStream inputStream, String mimeType) {
        Response response;
        String rangeRequest = session.getHeaders().get("range");

        try {
            // Calculate etag
            String etag = Integer.toHexString(inputStream.hashCode());

            // Support skipping:
            long startFrom = 0;
            long endAt = -1;
            if (rangeRequest != null) {
                if (rangeRequest.startsWith("bytes=")) {
                    rangeRequest = rangeRequest.substring("bytes=".length());
                    int minus = rangeRequest.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(rangeRequest.substring(0, minus));
                            endAt = Long.parseLong(rangeRequest.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // Change return code and add Content-Range header when skipping is requested
            long streamLength = inputStream.available();
            if (rangeRequest != null && startFrom >= 0) {
                if (startFrom >= streamLength) {
                    response = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, "");
                    response.addHeader("Content-Range", "bytes 0-0/" + streamLength);
                    response.addHeader("ETag", etag);
                } else {
                    if (endAt < 0) {
                        endAt = streamLength - 1;
                    }
                    long newLen = endAt - startFrom + 1;
                    if (newLen < 0) {
                        newLen = 0;
                    }

                    final long dataLen = newLen;
                    inputStream.skip(startFrom);

                    response = createResponse(Response.Status.PARTIAL_CONTENT, mimeType, inputStream);
                    response.addHeader("Content-Length", "" + dataLen);
                    response.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + streamLength);
                    response.addHeader("ETag", etag);
                }
            } else {
                if (etag.equals(session.getHeaders().get("if-none-match")))
                    response = createResponse(Response.Status.NOT_MODIFIED, mimeType, "");
                else {
                    response = createResponse(Response.Status.OK, mimeType, inputStream);
                    response.addHeader("Content-Length", "" + streamLength);
                    response.addHeader("ETag", etag);
                }
            }
        } catch (IOException | NullPointerException ioe) {
            response = getResponse("Forbidden: Reading file failed");
        }

        return (response == null) ? getResponse("Error 404: File not found") : response;
    }

    private Response createResponse(Status status, String mimeType, InputStream message) {
        Response response = NanoHTTPD.newChunkedResponse(status, mimeType, message);
        response.addHeader("Accept-Ranges", "bytes");
        return response;
    }

    private Response createResponse(Status status, String mimeType, String message) {
        Response response = NanoHTTPD.newFixedLengthResponse(status, mimeType, message);
        response.addHeader("Accept-Ranges", "bytes");
        return response;
    }

    private Response getResponse(String message) {
        return createResponse(Response.Status.OK, "text/plain", message);
    }

    private boolean isFontFile(String file) {
        for (String font : fonts) {
            if (file.endsWith(font)) {
                return true;
            }
        }
        return false;
    }
}