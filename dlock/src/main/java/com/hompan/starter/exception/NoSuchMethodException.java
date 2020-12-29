package com.hompan.starter.exception;

public class NoSuchMethodException extends RuntimeException{

    public NoSuchMethodException() {}

    public NoSuchMethodException(String message) {
        super(message);
    }
}
