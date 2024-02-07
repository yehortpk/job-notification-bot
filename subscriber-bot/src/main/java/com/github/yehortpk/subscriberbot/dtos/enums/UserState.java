package com.github.yehortpk.subscriberbot.dtos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Current state of the user
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum UserState {
    START_STATE
}
