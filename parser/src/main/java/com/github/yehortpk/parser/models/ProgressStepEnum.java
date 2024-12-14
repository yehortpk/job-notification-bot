package com.github.yehortpk.parser.models;

import lombok.Getter;

@Getter
public enum ProgressStepEnum {
    STEP_UNKNOWN(-1),
    STEP_ERROR(0),
    STEP_DONE(1);

    private final int value;

    ProgressStepEnum(int value) {
        this.value = value;
    }
}
