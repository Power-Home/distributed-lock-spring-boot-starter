package com.hompan.starter.exception;

public class IllegalReturnException extends RuntimeException{

    public IllegalReturnException() {}

    public IllegalReturnException(String message) {
        super(message);
    }
}
