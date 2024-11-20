package com.cryptomanager.exceptions;

public class TransactionServiceException extends RuntimeException {
  public TransactionServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
