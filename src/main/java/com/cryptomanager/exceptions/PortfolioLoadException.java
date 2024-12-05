package com.cryptomanager.exceptions;

import java.io.IOException;

/**
 * Excecao responsavel por lidar com o carregamento do portfólio.
 */
public class PortfolioLoadException extends RuntimeException {
    /** Construtor PortfolioLoadException.
     * @param message Recebe a mensagem informando o erro.
     * @param cause Recebe uma instancia de Throwable que ocasionou o lancamento da excecao.
     */
    public PortfolioLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}