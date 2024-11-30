package com.cryptomanager.exceptions;

public class PortfolioHasInvestmentsException extends RuntimeException {
    public PortfolioHasInvestmentsException(String message) {
        super(message);
    }
}