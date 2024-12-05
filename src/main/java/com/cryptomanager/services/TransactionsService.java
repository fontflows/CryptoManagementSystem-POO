package com.cryptomanager.services;

import com.cryptomanager.exceptions.TransactionServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;

import static com.cryptomanager.repositories.TransactionsRepository.*;


/** Classe responsavel pelos metodos Service de registro e leitura do histórico de transações de clientes */
@Service
public class TransactionsService {
    /**
     * Obtem o historico das transações realizadas em todo o programa baseado no tipo de transação.
     * @param transactionType Tipo de transacao solicitado : {@code SELL}, {@code BUY}, {@code CONVERSION} ou {@code ALL}.
     * @return {@code String} Lista com o historico ja formatado.
     * @throws TransactionServiceException Caso haja algum erro no carregamento do historico.
     */
    public String getTransactionHistory(String transactionType){
        try {
            String history = "";
            if (transactionType.equals("BUY") || transactionType.equals("SELL") || transactionType.equals("CONVERSION"))
                history = listToString(loadTransactions(transactionType), transactionType);

            else if (transactionType.equals("ALL"))
                history = allListsToString();

            if(history.isEmpty()) { throw new NoSuchElementException("Nenhuma transação encontrada"); }

            else { return history; }

        } catch (IOException e) {
            throw new TransactionServiceException("Erro interno do servidor ao carregar histórico de transações", e);

        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw new TransactionServiceException("Erro ao carregar histórico de transações: " + e.getMessage(), e);
        }
    }

    /**
     * Obtem o historico das transacoes realizadas por um cliente baseado no tipo de transacao.
     * @param userID Recebe o identificador do cliente de interesse.
     * @param transactionType Tipo de transacao solicitado {@code SELL}, {@code BUY}, {@code CONVERSION} ou {@code ALL}.
     * @return {@code String} Lista com o historico ja formatado.
     * @throws TransactionServiceException Caso haja algum erro no carregamento do historico.
     */
    public String getTransactionHistoryByID(String transactionType, String userID){
        try {
            String history = "";
            if (transactionType.equals("BUY") || transactionType.equals("SELL") || transactionType.equals("CONVERSION"))
                history =  listToString(loadTransactionsByID(transactionType, userID), transactionType);

            else if (transactionType.equals("ALL"))
                history = allListsToStringByID(userID);

            if(history.isEmpty()) { throw new NoSuchElementException("Nenhuma transação encontrada"); }

            else { return history; }

        } catch (IOException e) {
            throw new TransactionServiceException("Erro interno do servidor ao carregar histórico de transações", e);

        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw new TransactionServiceException("Erro ao carregar histórico de transações: " + e.getMessage(), e);
        }
    }
}
