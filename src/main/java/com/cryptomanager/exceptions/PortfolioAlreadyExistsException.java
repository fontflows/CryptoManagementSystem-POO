package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar a existencia previa de um portfolio.
 */
public class PortfolioAlreadyExistsException extends RuntimeException {
    /** Construtor PortfolioAlreadyExistsException.
     * @param message Recebe a mensagem informando o erro.
     */
    public PortfolioAlreadyExistsException(String message) {
        super(message);
    }
}