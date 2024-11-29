package com.cryptomanager.models;

/**
 *  Classe que trata da resposta do erro capturado pelo controller GlobalExceptionHandler
 */

public class ErrorResponse {
    private String message;
    private String details;

    public ErrorResponse(int value, String message, String details) {
        this.message = message;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}