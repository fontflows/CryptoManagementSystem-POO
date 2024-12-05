package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar potenciais erros na execucao do servico associado aos relatorios.
 */
public class ReportExceptions extends RuntimeException {
    /** Construtor ReportExceptions.
     * @param message Recebe a mensagem informando o erro.
     * @param cause Recebe uma instancia de Throwable que ocasionou o lancamento da excecao.
     */
    public ReportExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}