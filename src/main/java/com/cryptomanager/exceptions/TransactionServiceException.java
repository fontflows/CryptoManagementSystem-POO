package com.cryptomanager.exceptions;

/**
 * Excecao responsavel por tratar potenciais erros na execucao do servico associado as transacoes ocorridas no sistema.
 */
public class TransactionServiceException extends RuntimeException {
  /** Construtor TransactionsServiceException.
   * @param message Recebe a mensagem informando o erro.
   * @param cause Recebe uma instancia de Throwable que ocasionou o lancamento da excecao.
   */
  public TransactionServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}