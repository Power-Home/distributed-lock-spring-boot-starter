package com.hompan.starter.exception;

public class TimeoutException extends RuntimeException{

    public TimeoutException() {}

    public TimeoutException(String message) {
        super(message);
    }
}
