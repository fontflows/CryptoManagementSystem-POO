package com.cryptomanager.exceptions;

/**
 * Exceção responsável por tratar casos em que um suposto portfólio não foi encontrado.
 */
public class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}