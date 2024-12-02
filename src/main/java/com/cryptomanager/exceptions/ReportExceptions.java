package com.cryptomanager.exceptions;

/**
 * Exceção responsavel por tratar potenciais erros na execucao do servico associado aos relatorios.
 */
public class ReportExceptions extends RuntimeException {
    public ReportExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}