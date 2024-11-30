package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar a existencia previa de um portfolio.
 */
public class PortfolioAlreadyExistsException extends RuntimeException {
    public PortfolioAlreadyExistsException(String message) {
        super(message);
    }
}