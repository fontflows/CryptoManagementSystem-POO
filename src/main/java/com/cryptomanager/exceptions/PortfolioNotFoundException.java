package com.cryptomanager.exceptions;

public class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}