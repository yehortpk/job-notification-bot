package com.github.yehortpk.parser.exceptions;

import lombok.Getter;
import lombok.experimental.StandardException;

@StandardException
@Getter
public class RequestMetadataParamNotImplemented extends RuntimeException {
    private String param;

    public RequestMetadataParamNotImplemented(String param) {
        super(String.format("Request metadata param wasn't implemented %s", param));
        this.param = param;
    }
}
