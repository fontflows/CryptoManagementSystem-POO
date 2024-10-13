package com.cryptomanager.repositories;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;
import com.cryptomanager.models.Portfolio;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PortfolioRepository {
    private static final String FILE_PATH = "portfolio.txt";

    // Método para adicionar um portfólio
    public void addPortfolio(Portfolio portfolio) {
        if (!isValidPortfolio(portfolio)) {
            System.err.println("Erro: Portfólio inválido.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(portfolio.getId() + "," + portfolio.getUserId() + "\n");
            for (Investment investment : portfolio.getInvestments()) {
                if (!portfolio.hasAsset(investment.getCryptoCurrency().getName())) {
                    writer.write(portfolio.getId() + "," + portfolio.getUserId() + "," +
                            investment.getCryptoCurrency().getName() + "," +
                            investment.getCryptoInvestedQuantity() + "," +
                            investment.getPurchasePrice() + "\n");
                } else {
                    System.err.println("Erro: Ativo já existe no portfólio: " + investment.getCryptoCurrency().getName());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao adicionar portfólio: " + e.getMessage());
        }
    }

    // Método para carregar o portfólio de um usuário específico
    public List<Portfolio> loadPortfolioByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            System.err.println("Erro: userId não pode ser nulo ou vazio.");
            return new ArrayList<>();
        }

        List<Portfolio> portfolioList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 5) continue; // Verifica se a linha tem dados suficientes

                String portfolioId = parts[0];
                String userIdFromFile = parts[1];

                if (userIdFromFile.equals(userId)) {
                    String cryptoName = parts[2];
                    int quantity = Integer.parseInt(parts[3]);
                    double purchasePrice = Double.parseDouble(parts[4]);

                    Portfolio portfolio = portfolioList.stream()
                            .filter(p -> p.getId().equals(portfolioId))
                            .findFirst()
                            .orElse(new Portfolio(portfolioId, userIdFromFile));

                    if (!portfolioList.contains(portfolio)) {
                        portfolioList.add(portfolio);
                    }

                    CryptoCurrency cryptoCurrency = new CryptoCurrency(cryptoName, purchasePrice);
                    portfolio.addAsset(cryptoCurrency, purchasePrice, quantity);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar portfólio: " + e.getMessage());
        }
        return portfolioList;
    }

    // Método para remover um portfólio pelo ID e userId
    public void removePortfolio(String portfolioId, String userId, String assetName) {
        if (portfolioId == null || portfolioId.isEmpty() || userId == null || userId.isEmpty()) {
            System.err.println("Erro: portfolioId e userId não podem ser nulos ou vazios.");
            return;
        }

        List<Portfolio> portfolios = loadPortfolioByUserId(userId);
        Portfolio portfolioToRemoveFrom = portfolios.stream()
                .filter(p -> p.getId().equals(portfolioId))
                .findFirst()
                .orElse(null);

        if (portfolioToRemoveFrom != null && portfolioToRemoveFrom.hasAsset(assetName)) {
            List<Investment> newInvestments = new ArrayList<>();
            for (Investment investment : portfolioToRemoveFrom.getInvestments()) {
                if (!investment.getCryptoCurrency().getName().equals(assetName)) {
                    newInvestments.add(investment);
                }
            }
            portfolioToRemoveFrom.getInvestments().clear();
            portfolioToRemoveFrom.getInvestments().addAll(newInvestments);
        } else {
            System.err.println("Erro: Ativo não encontrado no portfólio ou portfólio inválido.");
        }

        // Reescrever o arquivo, omitindo o ativo removido (omita a parte do código aqui para simplificação)
    }


    // Método de validação de portfólio
    private boolean isValidPortfolio(Portfolio portfolio) {
        if (portfolio == null) {
            return false;
        }
        if (portfolio.getUserId() == null || portfolio.getUserId().isEmpty()) {
            System.err.println("Erro: userId não pode ser nulo ou vazio.");
            return false;
        }
        if (portfolio.getId() == null || portfolio.getId().isEmpty()) {
            System.err.println("Erro: portfolioId não pode ser nulo ou vazio.");
            return false;
        }
        if (portfolio.getInvestments() == null || portfolio.getInvestments().isEmpty()) {
            System.err.println("Erro: Portfólio deve conter pelo menos um ativo.");
            return false;
        }
        return true;
    }

    public void displayPortfolioInfo(Portfolio portfolio) {
        for (Investment investment : portfolio.getInvestments()) {
            String assetName = investment.getCryptoCurrency().getName();
            System.out.println("Ativo: " + assetName);
            System.out.println("Quantidade: " + portfolio.getAssetAmount(assetName));
            System.out.println("Preço de compra: " + investment.getPurchasePrice());

            // Usando hasAsset para uma verificação adicional
            if (portfolio.hasAsset(assetName)) {
                System.out.println(assetName + " está no portfólio.");
            } else {
                System.out.println(assetName + " não está no portfólio.");
            }
        }
    }
}