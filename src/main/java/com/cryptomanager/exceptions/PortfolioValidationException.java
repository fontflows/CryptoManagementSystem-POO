package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por lidar com a validacao do portfolio.
 */
public class PortfolioValidationException extends RuntimeException {
    public PortfolioValidationException(String message) {
        super(message);
    }
}