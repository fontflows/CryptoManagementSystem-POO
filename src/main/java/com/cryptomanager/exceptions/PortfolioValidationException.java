package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por lidar com a validacao do portfolio.
 */
public class PortfolioValidationException extends RuntimeException {
    /** Construtor PortfolioValidationException.
     * @param message Recebe a mensagem informando o erro.
     */
    public PortfolioValidationException(String message) {
        super(message);
    }
}