package com.cryptomanager.exceptions;

public class ClientServiceException extends RuntimeException {
    public ClientServiceException(String message, Throwable cause) { super (message,cause);}
}