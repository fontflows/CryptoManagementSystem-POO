package com.cryptomanager.exceptions;

/**
 * Exceção responsável por tratar investimentos já existentes.
 */
public class PortfolioHasInvestmentsException extends RuntimeException {
    public PortfolioHasInvestmentsException(String message) {
        super(message);
    }
}