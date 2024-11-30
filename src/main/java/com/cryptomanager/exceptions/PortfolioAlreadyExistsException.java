package com.cryptomanager.exceptions;

/**
 * Exceção responsável por tratar a existência prévia de um portfolio.
 */
public class PortfolioAlreadyExistsException extends RuntimeException {
    public PortfolioAlreadyExistsException(String message) {
        super(message);
    }
}