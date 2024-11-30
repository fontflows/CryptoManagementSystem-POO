package com.cryptomanager.exceptions;

import java.io.IOException;

/**
 * Exceção responsável por lidar com o carregamento do portfólio.
 */
public class PortfolioLoadException extends RuntimeException {
    public PortfolioLoadException(String message, IOException e) {
        super(message, e);
    }
}