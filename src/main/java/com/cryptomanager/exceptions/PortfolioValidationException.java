package com.cryptomanager.exceptions;

/**
 * Exceção responsável por lidar com a validação do portfólio.
 */
public class PortfolioValidationException extends RuntimeException {
    public PortfolioValidationException(String message) {
        super(message);
    }
}