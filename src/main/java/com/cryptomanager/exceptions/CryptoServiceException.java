package com.cryptomanager.exceptions;

public class CryptoServiceException extends RuntimeException {
    public CryptoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
