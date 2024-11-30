package com.cryptomanager.exceptions;

import java.io.IOException;

public class PortfolioLoadException extends RuntimeException {
    public PortfolioLoadException(String message, IOException e) {
        super(message, e);
    }
}