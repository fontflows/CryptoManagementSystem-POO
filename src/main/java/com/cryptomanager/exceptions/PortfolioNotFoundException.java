package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar casos em que um suposto portfolio não foi encontrado.
 */
public class PortfolioNotFoundException extends RuntimeException {
    /** Construtor PortfolioNotFoundException.
     * @param message Recebe a mensagem informando o erro.
     * @param cause Recebe uma instancia de Throwable que ocasionou o lancamento da excecao.
     */
    public PortfolioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}