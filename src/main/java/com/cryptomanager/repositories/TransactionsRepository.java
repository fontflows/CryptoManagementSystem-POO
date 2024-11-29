package com.cryptomanager.repositories;

import com.cryptomanager.models.Investment;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Classe responsavel pelos metodos de manipulacao dos arquivos que armazenam as transacoes realizadas no programa.*/
@Repository
public class TransactionsRepository {
    private static final String FILE_PATH = "transactionsHistory.txt";

    /** Salvar transacao de compra no arquivo.
     * @param userID Identificador do usuario que realisou a transacao.
     * @param portfolioID Identificador do portfolio do usuario que realizou a transacao.
     * @param investment Armazena as informacoes da transacao.
     * @throws IOException Caso ocorra um erro na escrita dos dados no arquivo.
     */
    public static void saveBuyTransaction(String userID, String portfolioID, Investment investment) throws IOException {
        saveTradeTransactions(userID, portfolioID, "BUY", investment);
    }

    /** Salvar transacao de venda no arquivo.
     * @param userID Identificador do usuario que realisou a transacao.
     * @param portfolioID Identificador do portfolio do usuario que realizou a transacao.
     * @param investment Armazena as informacoes da transacao.
     * @throws IOException Caso ocorra um erro na escrita dos dados no arquivo.
     */
    public static void saveSellTransaction(String userID, String portfolioID, Investment investment) throws IOException {
        saveTradeTransactions(userID, portfolioID, "SELL", investment);
    }

    /** Salvar transacao de conversao no arquivo.
     * @param userID Identificador do usuario que realisou a transacao.
     * @param portfolioID Identificador do portfolio do usuario que realizou a transacao.
     * @param fromCrypto Nome da criptomoeda que sera convertida.
     * @param toCrypto Nome da criptomoeda que sera recebida.
     * @param amount Quantidade da criptomoeda que sera convertida.
     * @param conversionRate Taxa de conversao entre as criptomoedas envolvidas.
     * @param value Valor total envolvido na operacao.
     * @throws IOException Caso ocorra um erro na escrita dos dados no arquivo.
     */
    public static void saveConversionTransaction(String userID, String portfolioID, String fromCrypto, String toCrypto, double amount, double conversionRate, double value) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DecimalFormat formatter = new DecimalFormat("#.##");
            String rate = formatter.format(conversionRate);
            writer.write(localDateTime.format(dateTimeFormatter) + "," + userID + "," + portfolioID + "," + "CONVERSION" + "," + fromCrypto + "," + toCrypto + "," + amount + "," + "1:" + rate + "," + value);
            writer.newLine();
        }
    }

    /** Salvar transacao de compra ou venda no arquivo.
     * @param userID Identificador do usuario que realisou a transacao.
     * @param portfolioID Identificador do portfolio do usuario que realizou a transacao.
     * @param transactionType Tipo de transacao que sera salvo.
     * @param investment Armazena as informacoes da transacao.
     * @throws IOException Caso ocorra um erro na escrita dos dados no arquivo.
     */
    private static void saveTradeTransactions(String userID, String portfolioID, String transactionType, Investment investment) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            writer.write(localDateTime.format(dateTimeFormatter) + "," + userID + "," + portfolioID + "," + transactionType + "," + investment.toString());
            writer.newLine();
        }
    }

    /** Carrega as transacoes armazenadas no arquivo baseado no tipo de transacao.
     * @param transactionType Tipo de transacao que sera carregado.
     * @return {@code List<String>} Lista com todas as transacoes com o tipo especificado.
     * @throws IOException Caso ocorra um erro na leitura dos dados no arquivo.
     */
    public static List<String> loadTransactions(String transactionType) throws IOException {
        List<String> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String[] parts;
            while ((line = reader.readLine()) != null) {
                parts = line.split(",");
                if (parts[3].equalsIgnoreCase(transactionType) || transactionType.equalsIgnoreCase("ALL")) {
                    transactions.add(line);
                }
            }
        }
        return transactions;
    }

    /** Carrega as transacoes de um usuario especifico baseado no tipo de transacao.
     * @param transactionType Tipo de transacao que sera carregado.
     * @param userID Identificador do usuario cujas transacoes serao carregadas
     * @return {@code List<String>} Lista com todas as transacoes do usuario com o tipo especificado.
     * @throws IOException Caso ocorra um erro na leitura dos dados no arquivo.
     */
    public static List<String> loadTransactionsByID(String transactionType, String userID) throws IOException {
        List<String> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String[] parts;
            while ((line = reader.readLine()) != null) {
                parts = line.split(",");
                if (parts[1].equalsIgnoreCase(userID.trim()) && parts[3].equalsIgnoreCase(transactionType)) {
                    transactions.add(line);
                }
            }
        }
        return transactions;
    }

    /** Obtem as transacoes contidas em uma lista de maneira formatada.
     * @param transactions Lista com todas as transacoes que devem ser formatadas.
     * @param transactionType Tipo de transacao que sera formatado.
     * @return {@code String} Transacoes da lista formatadas em uma String.
     */
    public String listToString(List<String> transactions, String transactionType) {
        if(transactions.isEmpty()) { return ""; }
        StringBuilder history = new StringBuilder();
        if(transactionType.equalsIgnoreCase("BUY") || transactionType.equalsIgnoreCase("SELL")) {
            history.append("DATE | USER-ID | PORTFOLIO-ID | TRANSACTION-TYPE | CRYPTOCURRENCY | AMOUNT | PRICE |\n");
        }
        else if(transactionType.equalsIgnoreCase("CONVERSION")) {
            history.append("DATE | USER-ID | PORTFOLIO-ID | TRANSACTION-TYPE | FROM-CRYPTOCURRENCY | TO-CRYPTOCURRENCY | AMOUNT | CONVERSION-RATE | VALUE |\n");
        }
        for (String transaction : transactions) {
            String[] parts = transaction.split(",");
            for (String part : parts) {
                history.append(part);
                history.append(" | ");
            }
            history.append("\n");
        }
        history.append("\n");
        return history.toString();
    }

    /** Obtem todas as transacoes registradas no programa de maneira formatada.
     * @return {@code String} Todas transacoes formatadas em uma String.
     * @throws IOException Caso ocorra um erro na leitura dos dados no arquivo.
     */
    public String allListsToString() throws IOException {
        return listToString(loadTransactions("BUY"), "BUY") + listToString(loadTransactions("SELL"), "SELL") + listToString(loadTransactions("CONVERSION"), "CONVERSION");
    }

    /** Obtem todas as transacoes de um usuario de maneira formatada.
     * @return {@code String} Todas transacoes de um usuario formatadas em uma String.
     * @throws IOException Caso ocorra um erro na leitura dos dados no arquivo.
     */
    public String allListsToStringByID(String userID) throws IOException {
        return listToString(loadTransactionsByID("BUY", userID), "BUY") + listToString(loadTransactionsByID("SELL", userID), "SELL") + listToString(loadTransactionsByID("CONVERSION", userID), "CONVERSION");
    }
}
