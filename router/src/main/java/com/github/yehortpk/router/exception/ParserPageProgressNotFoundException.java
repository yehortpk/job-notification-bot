package com.github.yehortpk.router.exception;

import lombok.experimental.StandardException;

@StandardException
public class ParserPageProgressNotFoundException extends RuntimeException {
    public ParserPageProgressNotFoundException() {
        super("Parser page progress with this page not found");
    }
}
