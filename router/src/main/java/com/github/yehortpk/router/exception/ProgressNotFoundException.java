package com.github.yehortpk.router.exception;

import lombok.experimental.StandardException;

@StandardException
public class ProgressNotFoundException extends RuntimeException {
    public ProgressNotFoundException() {
        super("Progress with this hash not found");
    }
}
