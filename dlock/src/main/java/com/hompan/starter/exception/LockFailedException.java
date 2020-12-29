package com.hompan.starter.exception;

public class LockFailedException extends RuntimeException{

    public LockFailedException() {}

    public LockFailedException(String message) {
        super(message);
    }
}
