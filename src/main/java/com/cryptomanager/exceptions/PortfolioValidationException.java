package com.cryptomanager.exceptions;

public class PortfolioValidationException extends RuntimeException {
    public PortfolioValidationException(String message) {
        super(message);
    }
}