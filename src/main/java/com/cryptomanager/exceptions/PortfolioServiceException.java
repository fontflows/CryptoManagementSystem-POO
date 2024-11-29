package com.cryptomanager.exceptions;

public class PortfolioServiceException extends RuntimeException{
    public PortfolioServiceException(String message){
        super(message);
    }
}