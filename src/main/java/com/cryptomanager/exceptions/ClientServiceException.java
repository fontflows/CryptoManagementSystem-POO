package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar potenciais erros na execucao do servico associado ao cliente.
 */
public class ClientServiceException extends RuntimeException {
    public ClientServiceException(String message, Throwable cause) { super (message,cause);}
}