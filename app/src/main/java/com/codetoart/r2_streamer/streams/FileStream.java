package com.codetoart.r2_streamer.streams;

import com.codetoart.r2_streamer.streams.seekableinputstream.SeekableInputStream;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by Shrikant Badwaik on 01-Feb-17.
 */

public class FileStream extends SeekableInputStream {
    private final String TAG = "FileInputStream";

    public int offset;
    public int length;
    private String filePath;
    private boolean bytesAvailable;
    private RandomAccessFile fileHandle;
    private Status fileStatus = Status.NOT_OPEN;

    public FileStream(String path) {
        super();
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        int fileSize = ((int) file.length());
        if (fileSize != 0) {
            this.length = fileSize;
        }
        this.filePath = path;
    }

    /*public Status getFileStatus() {
        return fileStatus;
    }

    @Override
    public int getOffset() {
        try {
            if (fileHandle.getFilePointer() == 0) {
                return offset;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public boolean isBytesAvailable() {
        return (offset < length);
    }

    @Override
    public void openFile() {
        try {
            this.fileHandle = new RandomAccessFile(filePath, "r");
            this.fileStatus = Status.valueOfEnum("Opened");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean getBufferStatus(int[] buffer, int availableData) {
        return false;
    }

    @Override
    public int readFile(int[] buffer, int maxLength) {      //List<Integer> buffer
        try {
            byte[] fileData = new byte[(byte) fileHandle.length()];     //buffer into which data is read?

            //int fileData = fileHandle.read(b, 0, maxLength);
            fileHandle.readFully(fileData, 0, maxLength);   //readUTF()? fileData-read data into, 0-file pointer/offset, maxLength-no of bytes to read

            if (fileData.length != 0) {
                if (fileData.length < maxLength) {
                    this.fileStatus = Status.valueOfEnum("At End");
                }
                for (int i = 0; i < buffer.length; i++) {
                    buffer[i] = fileData[i];
                }
                return fileData.length;

            } else {
                this.fileStatus = Status.valueOfEnum("Error");

                Log.e(TAG, "Error reading file");
                //throw new RuntimeException("Error reading file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void seek(int offset, SeekWhence whence) {
        try {
            if (whence != SeekWhence.startOfFile)       //==
                throw new RuntimeException("Only seek from start of stream is supported for now.");
            if (offset <= 0)        //>=
                throw new RuntimeException("Since only seek from start of stream if supported, offset must be >= 0");

            fileHandle.seek(offset);
        } catch (IOException e) {
            this.fileStatus = Status.valueOfEnum("Error");
            e.printStackTrace();
        }
    }

    @Override
    public void closeFile() {
        try {
            this.fileHandle.close();
            this.fileStatus = Status.valueOfEnum("Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}