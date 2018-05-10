package org.readium.r2_streamer.model.container;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Shrikant Badwaik on 24-Jan-17.
 */

public class EpubContainer implements Container {
    private final String TAG = "EpubContainer";
    private ZipFile zipFile;
    private net.lingala.zip4j.core.ZipFile zipFile4J;

    public EpubContainer(String epubFilePath) throws IOException {
        this.zipFile = new ZipFile(epubFilePath);
        try {
            zipFile4J = new net.lingala.zip4j.core.ZipFile(epubFilePath);
        } catch (ZipException e) {
            System.err.println(TAG + " -> " + e);
        }

        System.out.println(TAG + " Reading epub at path: " + epubFilePath);
    }

    @Override
    public String rawData(String relativePath) throws NullPointerException {
        System.out.println(TAG + " Reading file at path: " + relativePath);
        try {
            ZipEntry zipEntry = zipFile.getEntry(relativePath);
            if (zipEntry == null) {
                return "";
            }
            InputStream is = zipFile.getInputStream(zipEntry);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);        //.append('\n');
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String newRawData(String relativePath) throws NullPointerException {
        System.out.println(TAG + " Reading file at path: " + relativePath);
        try {
            FileHeader fileHeader = zipFile4J.getFileHeader(relativePath);
            if (fileHeader == null)
                return null;
            InputStream is = zipFile4J.getInputStream(fileHeader);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);        //.append('\n');
            }

            return sb.toString();
        } catch (ZipException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int rawDataSize(String relativePath) {
        ZipEntry zipEntry = zipFile.getEntry(relativePath);
        return ((int) zipEntry.getSize());
    }

    public int newRawDataSize(String relativePath) {

        FileHeader fileHeader = null;
        try {
            fileHeader = zipFile4J.getFileHeader(relativePath);
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
        Enumeration zipEntries = zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            String fileName = ((ZipEntry) zipEntries.nextElement()).getName();
            files.add(fileName);
        }
        return files;
    }

    public List<String> newListFiles() {

        List<String> files = new ArrayList<>();
        List<FileHeader> fileHeaderList;
        try {
            fileHeaderList = zipFile4J.getFileHeaders();
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

                    System.out.println(" -> rawData -> " + (rawData(relativePath)
                            .equals(newRawData(relativePath)) ? "equal" : "not equal"));

                    System.out.println(" -> " + listFiles());
                    System.out.println(" -> " + newListFiles());

                    System.out.println(" -> dataSize -> " + (rawDataSize(relativePath) ==
                            newRawDataSize(relativePath) ? "equal" : "not equal"));

                    FileHeader fileHeader = zipFile4J.getFileHeader(relativePath);
                    InputStream inputStream = zipFile4J.getInputStream(fileHeader);

//                    ZipEntry zipEntry = zipFile.getEntry(relativePath);
//                    InputStream inputStream = zipFile.getInputStream(zipEntry);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    long BUFFER_SIZE = 16 * 1024;
                    byte[] byteArray = new byte[(int) BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(byteArray)) != -1) {
                        byteArrayOutputStream.write(byteArray, 0, bytesRead);
                    }

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