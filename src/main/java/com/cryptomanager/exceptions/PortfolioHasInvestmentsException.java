package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar investimentos ja existentes.
 */
public class PortfolioHasInvestmentsException extends RuntimeException {
    public PortfolioHasInvestmentsException(String message) {
        super(message);
    }
}