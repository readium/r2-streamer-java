package com.codetoart.r2_streamer.streams;

/**
 * Created by Shrikant Badwaik on 03-Feb-17.
 */

public enum Status {
    NOT_OPEN("Not Open"), OPENED("Opened"), CLOSED("Closed"), AT_END("At End"), ERROR("Error");

    String value;

    Status(String value) {
        this.value = value;
    }

    public static Status valueOfEnum(String name) {
        for (Status fileStatus : Status.values()) {
            if (fileStatus.value.equals(name)) {
                return fileStatus;
            }
        }
        throw new IllegalArgumentException(name);
    }
}
