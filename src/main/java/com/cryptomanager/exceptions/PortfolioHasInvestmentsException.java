package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar investimentos ja existentes.
 */
public class PortfolioHasInvestmentsException extends RuntimeException {
    /** Construtor PortfolioHasInvestmentsException.
     * @param message Recebe a mensagem informando o erro.
     */
    public PortfolioHasInvestmentsException(String message) {
        super(message);
    }
}