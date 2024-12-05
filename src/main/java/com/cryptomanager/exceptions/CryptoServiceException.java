package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar potenciais erros na execucao do servico associado a criptomoeda.
 */
public class CryptoServiceException extends RuntimeException {
    /** Construtor CryptoServiceException.
     * @param message Recebe a mensagem informando o erro.
     * @param cause Recebe uma instancia de Throwable que ocasionou o lancamento da excecao.
     */
    public CryptoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}