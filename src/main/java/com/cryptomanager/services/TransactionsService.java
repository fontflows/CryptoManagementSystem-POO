package com.cryptomanager.services;

import com.cryptomanager.exceptions.TransactionServiceException;
import com.cryptomanager.repositories.TransactionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TransactionsService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsService.class);
    private final TransactionsRepository transactionsRepository;

    @Autowired
    public TransactionsService(TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    public String getTransactionHistory(String transactionType){
        try {
            return transactionsRepository.listToString(transactionsRepository.loadTransactions(transactionType), transactionType);
        } catch (IOException e) {
            logger.error("Erro interno do servidor ao carregar histórico de transações", e);
            throw new TransactionServiceException("Erro interno do servidor ao carregar histórico de transações", e);
        } catch (NoSuchElementException e) {
            logger.error("Erro ao carregar histórico de transações", e);
            throw new TransactionServiceException("Erro ao carregar histórico de transações: " + e.getMessage(), e);
        }
    }

    public String getTransactionHistoryByID(String transactionType, String userID){
        try {
            switch (transactionType){
                case "BUY", "SELL", "CONVERSION":
                    return transactionsRepository.listToString(transactionsRepository.loadTransactionsByID(transactionType, userID), transactionType);
                case "ALL":
                    return transactionsRepository.listToString(transactionsRepository.loadTransactions("BUY"), "BUY") + transactionsRepository.listToString(transactionsRepository.loadTransactions("SELL"), "SELL") + transactionsRepository.listToString(transactionsRepository.loadTransactions("CONVERSION"), "CONVERSION");
                default: throw new IllegalArgumentException("Tipo de transação inválido");
            }
        } catch (IOException e) {
            logger.error("Erro interno do servidor ao carregar histórico de transações", e);
            throw new TransactionServiceException("Erro interno do servidor ao carregar histórico de transações", e);
        } catch (NoSuchElementException e) {
            logger.error("Erro ao carregar histórico de transações", e);
            throw new TransactionServiceException("Erro ao carregar histórico de transações: " + e.getMessage(), e);
        }
    }
}
