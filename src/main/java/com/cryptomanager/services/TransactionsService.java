package com.cryptomanager.services;

import com.cryptomanager.exceptions.TransactionServiceException;
import com.cryptomanager.repositories.TransactionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;

import static com.cryptomanager.repositories.TransactionsRepository.loadTransactions;
import static com.cryptomanager.repositories.TransactionsRepository.loadTransactionsByID;


/** Classe responsável pelos métodos Service de registro e leitura do histórico de transações de clientes */
@Service
public class TransactionsService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsService.class);
    private final TransactionsRepository transactionsRepository;

    /** Constructor TransactionsService
     * @param transactionsRepository Instância que conecta o Service à classe que manipula os dados no arquivo
     */
    @Autowired
    public TransactionsService(TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    /**
     * Obtém o histórico das transações realizadas em todo o programa baseado no tipo de transação.
     * @param transactionType Tipo de transação solicitado : {@code SELL}, {@code BUY}, {@code CONVERSION} ou {@code ALL}.
     * @return {@code String} Lista com o histórico já formatado.
     */
    public String getTransactionHistory(String transactionType){
        try {
            String history = "";
            if (transactionType.equals("BUY") || transactionType.equals("SELL") || transactionType.equals("CONVERSION")) {
                history =  transactionsRepository.listToString(loadTransactions(transactionType), transactionType);
            }
            else if (transactionType.equals("ALL")) {
                history = transactionsRepository.allListsToString();
            }
            if(history.isEmpty()) { throw new NoSuchElementException("Nenhuma transação encontrada"); }
            else { return history; }
        } catch (IOException e) {
            logger.error("Erro interno do servidor ao carregar histórico de transações", e);
            throw new TransactionServiceException("Erro interno do servidor ao carregar histórico de transações", e);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            logger.error("Erro ao carregar histórico de transações", e);
            throw new TransactionServiceException("Erro ao carregar histórico de transações: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém o histórico das transações realizadas por um cliente baseado no tipo de transação.
     * @param transactionType Tipo de transação solicitado {@code SELL}, {@code BUY}, {@code CONVERSION} ou {@code ALL}.
     * @return {@code String} Lista com o histórico já formatado.
     */
    public String getTransactionHistoryByID(String transactionType, String userID){
        try {
            String history = "";
            if (transactionType.equals("BUY") || transactionType.equals("SELL") || transactionType.equals("CONVERSION")) {
                history =  transactionsRepository.listToString(loadTransactionsByID(transactionType, userID), transactionType);
            }
            else if (transactionType.equals("ALL")) {
                history = transactionsRepository.allListsToStringByID(userID);
            }
            if(history.isEmpty()) { throw new NoSuchElementException("Nenhuma transação encontrada"); }
            else { return history; }
        } catch (IOException e) {
            logger.error("Erro interno do servidor ao carregar histórico de transações", e);
            throw new TransactionServiceException("Erro interno do servidor ao carregar histórico de transações", e);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            logger.error("Erro ao carregar histórico de transações", e);
            throw new TransactionServiceException("Erro ao carregar histórico de transações: " + e.getMessage(), e);
        }
    }
}
