package com.github.yehortpk.parser.models;

import lombok.Getter;

@Getter
public enum ProgressStepEnum {
    STEP_PENDING(-1),
    STEP_ERROR(0),
    STEP_DONE(1);

    private final int value;

    ProgressStepEnum(int value) {
        this.value = value;
    }
}
