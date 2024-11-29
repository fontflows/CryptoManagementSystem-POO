package com.cryptomanager.exceptions;

public class NoCryptosSuggestedException extends RuntimeException {
    public NoCryptosSuggestedException(String message) {
        super(message);
    }
}