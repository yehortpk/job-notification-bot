package com.github.yehortpk.parser.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PageProgressStatusEnum {
    STEP_PENDING(-1),
    STEP_ERROR(0),
    STEP_DONE(1);

    private final int value;

    PageProgressStatusEnum(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
