package com.cryptomanager.exceptions;

import java.io.IOException;

/**
 * Excecao responsavel por lidar com o carregamento do portfólio.
 */
public class PortfolioLoadException extends RuntimeException {
    public PortfolioLoadException(String message, IOException e) {
        super(message, e);
    }
}