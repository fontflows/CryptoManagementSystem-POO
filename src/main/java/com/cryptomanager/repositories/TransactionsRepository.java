package com.cryptomanager.repositories;

import com.cryptomanager.models.Investment;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class TransactionsRepository {
    private static final String FILE_PATH = "transactionsHistory.txt";

    public static void saveBuyTransaction(String userID, String portfolioID, Investment investment) throws IOException {
        saveTradeTransactions(userID, portfolioID, "BUY", investment);
    }

    public static void saveSellTransaction(String userID, String portfolioID, Investment investment) throws IOException {
        saveTradeTransactions(userID, portfolioID, "SELL", investment);
    }

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

    private static void saveTradeTransactions(String userID, String portfolioID, String transactionType, Investment investment) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            writer.write(localDateTime.format(dateTimeFormatter) + "," + userID + "," + portfolioID + "," + transactionType + "," + investment.toString());
            writer.newLine();
        }
    }

    public List<String> loadTransactions(String transactionType) throws IOException {
        List<String> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String[] parts;
            while ((line = reader.readLine()) != null) {
                parts = line.split(",");
                if(parts[3].equalsIgnoreCase(transactionType)) {
                    transactions.add(line);
                }
            }
        }
        if(transactions.isEmpty()) { throw new NoSuchElementException("Nenhuma transação encontrada"); }
        return transactions;
    }

    public List<String> loadTransactionsByID(String transactionType, String userID) throws IOException {
        List<String> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String[] parts;
            while ((line = reader.readLine()) != null) {
                parts = line.split(",");
                if(parts[1].equalsIgnoreCase(userID) && (parts[3].equalsIgnoreCase("ALL") || parts[3].equalsIgnoreCase(transactionType))) {
                    transactions.add(line);
                }
            }
        }
        if(transactions.isEmpty()) { throw new NoSuchElementException("Nenhuma transação encontrada"); }
        return transactions;
    }

    public String listToString(List<String> transactions, String transactionType) throws IOException {
        StringBuilder history = new StringBuilder();
        if(transactionType.equalsIgnoreCase("BUY") || transactionType.equalsIgnoreCase("SELL")) {
            history.append("DATE | USER-ID | PORTFOLIO-ID | TRANSACTION-TYPE | CRYPTOCURRENCY | AMOUNT | PRICE |\n\n");
            for (String transaction : transactions) {
                String[] parts = transaction.split(",");
                for(String part : parts) {
                    history.append(part);
                    history.append(" | ");
                }
                history.append("\n");
            }
        }
        else if (transactionType.equalsIgnoreCase("CONVERSION")) {
            history.append("DATE | USER-ID | PORTFOLIO-ID | TRANSACTION-TYPE | FROM-CRYPTOCURRENCY | TO-CRYPTOCURRENCY | AMOUNT | CONVERSION-RATE | VALUE |\n\n");
            for (String transaction : transactions) {
                String[] parts = transaction.split(",");
                for(String part : parts) {
                    history.append(part);
                    history.append(" | ");
                }
                history.append("\n");
            }
        }
        return history.toString();
    }
}
