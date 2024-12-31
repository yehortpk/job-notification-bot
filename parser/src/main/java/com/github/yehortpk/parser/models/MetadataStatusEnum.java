package com.github.yehortpk.parser.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MetadataStatusEnum {
    PENDING(-1),
    ERROR(0),
    DONE(1);

    private final int value;

    MetadataStatusEnum(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}