package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar potenciais erros na execucao do servico associado ao cliente.
 */
public class ClientServiceException extends RuntimeException {
    /** Construtor ClientServiceException.
     * @param message Recebe a mensagem informando o erro.
     * @param cause Recebe uma instancia de Throwable que ocasionou o lancamento da excecao.
     */
    public ClientServiceException(String message, Throwable cause) { super (message,cause);}
}