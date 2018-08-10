package org.readium.r2_streamer.model.container;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import org.readium.r2_streamer.parser.UnicodeBOMInputStream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Shrikant Badwaik on 24-Jan-17.
 */

public class EpubContainer implements Container {
    private final String TAG = "EpubContainer";
    private ZipFile zipFile;

    public EpubContainer(String epubFilePath) throws IOException {
        try {
            zipFile = new net.lingala.zip4j.core.ZipFile(epubFilePath);
        } catch (ZipException e) {
            System.err.println(TAG + " -> " + e);
        }

        System.out.println(TAG + " Reading epub at path: " + epubFilePath);
    }

    @Override
    public String rawData(String relativePath) throws NullPointerException {
        System.out.println(TAG + " Reading file at path: " + relativePath);

        try {
            String decodedRelativePath = new URI(relativePath).getPath();
            FileHeader fileHeader = zipFile.getFileHeader(decodedRelativePath);
            if (fileHeader == null)
                return null;

            InputStream inputStream = zipFile.getInputStream(fileHeader);
            UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(inputStream);
            ubis.skipBOM();

            BufferedReader br = new BufferedReader(new InputStreamReader(ubis));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }

            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);

            inputStream.close();
            ubis.close();
            return sb.toString();

        } catch (URISyntaxException | ZipException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int rawDataSize(String relativePath) {

        String decodedRelativePath = null;
        try {
            decodedRelativePath = new URI(relativePath).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        FileHeader fileHeader = null;
        try {
            fileHeader = zipFile.getFileHeader(decodedRelativePath);
        } catch (ZipException e) {
            e.printStackTrace();
        }
        if (fileHeader == null)
            return -1;
        return (int) fileHeader.getUncompressedSize();
    }

    @Override
    public List<String> listFiles() {

        List<String> files = new ArrayList<>();
        List<FileHeader> fileHeaderList;
        try {
            fileHeaderList = zipFile.getFileHeaders();
        } catch (ZipException e) {
            e.printStackTrace();
            return files;
        }
        if (fileHeaderList == null)
            return files;

        for (FileHeader fileHeader : fileHeaderList)
            files.add(fileHeader.getFileName());

        return files;
    }

    @Override
    public InputStream rawDataInputStream(final String relativePath) throws NullPointerException {
        try {
            Callable<ByteArrayInputStream> callable = new Callable<ByteArrayInputStream>() {
                @Override
                public ByteArrayInputStream call() throws Exception {

                    String decodedRelativePath = new URI(relativePath).getPath();
                    FileHeader fileHeader = zipFile.getFileHeader(decodedRelativePath);
                    InputStream inputStream = zipFile.getInputStream(fileHeader);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    long BUFFER_SIZE = 16 * 1024;
                    byte[] byteArray = new byte[(int) BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(byteArray)) != -1) {
                        byteArrayOutputStream.write(byteArray, 0, bytesRead);
                    }
                    inputStream.close();

                    byteArrayOutputStream.flush();
                    byte[] streamArray = byteArrayOutputStream.toByteArray();
                    return new ByteArrayInputStream(streamArray);
                }
            };

            ExecutorService executorService = Executors.newCachedThreadPool();
            Future<ByteArrayInputStream> future = executorService.submit(callable);
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}