package com.cryptomanager.exceptions;

/**
 * Exceção responsavel por tratar potenciais erros na execucao do servico associado a criptomoeda.
 */
public class CryptoServiceException extends RuntimeException {
    public CryptoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}