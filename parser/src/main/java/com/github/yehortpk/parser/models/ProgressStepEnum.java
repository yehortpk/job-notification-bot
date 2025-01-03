package com.github.yehortpk.parser.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProgressStepEnum {
    STEP_PENDING(-1),
    STEP_ERROR(0),
    STEP_DONE(1);

    private final int value;

    ProgressStepEnum(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
