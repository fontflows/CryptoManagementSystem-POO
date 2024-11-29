package com.cryptomanager.exceptions;

public class InvestmentStrategyNotFoundException extends RuntimeException {
    public InvestmentStrategyNotFoundException(String message) {
        super(message);
    }
}