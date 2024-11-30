package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar casos em que um suposto portfolio não foi encontrado.
 */
public class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}