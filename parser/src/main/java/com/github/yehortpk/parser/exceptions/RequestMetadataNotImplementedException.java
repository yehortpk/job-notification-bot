package com.github.yehortpk.parser.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class RequestMetadataNotImplementedException extends RuntimeException {
    public RequestMetadataNotImplementedException() {
        super("Binding params present, but SiteMetadata interface wasn't implemented in company config. You have" +
                "to implement it, or extend SiteMetadataImpl and override necessary methods");
    }
}
