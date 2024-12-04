package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar potenciais erros na execucao do servico associado as transacoes ocorridas no sistema.
 */
public class TransactionServiceException extends RuntimeException {
  public TransactionServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}