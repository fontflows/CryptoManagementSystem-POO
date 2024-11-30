package com.cryptomanager.exceptions;

public class PortfolioAlreadyExistsException extends RuntimeException {
    public PortfolioAlreadyExistsException(String message) {
        super(message);
    }
}