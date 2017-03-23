package com.example.stanleychin.myapplication.exceptions;

/**
 * Created by stanleychin on 2017-03-22.
 */

public class ResponseErrorException extends Exception {
    public ResponseErrorException(String message) {
        super(message);
    }

    public ResponseErrorException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
