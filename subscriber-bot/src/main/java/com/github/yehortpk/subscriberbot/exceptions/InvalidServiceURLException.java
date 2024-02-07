package com.github.yehortpk.subscriberbot.exceptions;

/**
 * Exception that can be thrown if service url doesn't pass URI standards
 */
public class InvalidServiceURLException extends RuntimeException{
    public InvalidServiceURLException(String message) {
        super(message);
    }
}
