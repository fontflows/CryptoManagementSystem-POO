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

    // Método para adicionar ou atualizar um portfólio
    public void addPortfolio(Portfolio portfolio) {
        if (!isValidPortfolio(portfolio)) {
            System.err.println("Erro: Portfólio inválido.");
            return;
        }

        // Carrega portfólios existentes
        List<Portfolio> existingPortfolios = loadPortfolioByUserId(portfolio.getUserId());

        // Atualiza ou adiciona investimentos
        for (Investment investment : portfolio.getInvestments()) {
            Portfolio existingPortfolio = null;
            for (Portfolio p : existingPortfolios) {
                if (p.getId().equals(portfolio.getId())) {
                    existingPortfolio = p;
                    break;
                }
            }

            if (existingPortfolio != null) {
                // Atualiza a quantidade e preço de compra se o ativo já existir
                if (existingPortfolio.hasAsset(investment.getCryptoCurrency().getName())) {
                    existingPortfolio.getInvestments().remove(investment);
                    existingPortfolio.addAsset(investment.getCryptoCurrency(), investment.getPurchasePrice(), investment.getCryptoInvestedQuantity());
                } else {
                    existingPortfolio.addAsset(investment.getCryptoCurrency(), investment.getPurchasePrice(), investment.getCryptoInvestedQuantity());
                }
            } else {
                portfolio.addAsset(investment.getCryptoCurrency(), investment.getPurchasePrice(), investment.getCryptoInvestedQuantity());
            }
        }
        // Salva todos os portfólios de volta ao arquivo
        savePortfolios(existingPortfolios);
    }

    // Método para salvar todos os portfólios no arquivo
    private void savePortfolios(List<Portfolio> portfolios) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portfolio portfolio : portfolios) {
                writer.write(portfolio.getId() + "," + portfolio.getUserId() + "\n");
                for (Investment investment : portfolio.getInvestments()) {
                    writer.write(portfolio.getId() + "," + portfolio.getUserId() + "," +
                            investment.getCryptoCurrency().getName() + "," +
                            investment.getCryptoInvestedQuantity() + "," +
                            investment.getPurchasePrice() + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar portfólios: " + e.getMessage());
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
                if (line.isEmpty())
                    continue;

                String[] parts = line.split(",");
                if (parts.length < 5) continue; // Verifica se a linha tem dados suficientes

                String portfolioId = parts[0];
                String userIdFromFile = parts[1];

                if (userIdFromFile.equals(userId)) {
                    String cryptoName = parts[2];
                    double quantity = Double.parseDouble(parts[3]);
                    double purchasePrice = Double.parseDouble(parts[4]);

                    Portfolio portfolio = null;
                    for (Portfolio p : portfolioList) {
                        if (p.getId().equals(portfolioId)) {
                            portfolio = p;
                            break;
                        }
                    }

                    if (portfolio == null) {
                        portfolio = new Portfolio(portfolioId, userIdFromFile);
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
            System.err.println("Erro: portfolioId e/ou userId não podem ser nulos ou vazios.");
            return;
        }

        List<Portfolio> portfolios = loadPortfolioByUserId(userId);
        Portfolio portfolioToRemoveFrom = null;
        for (Portfolio p : portfolios) {
            if (p.getId().equals(portfolioId)) {
                portfolioToRemoveFrom = p;
                break;
            }
        }

        if (portfolioToRemoveFrom != null && portfolioToRemoveFrom.hasAsset(assetName)) {
            List<Investment> newInvestments = new ArrayList<>();
            for (Investment investment : portfolioToRemoveFrom.getInvestments()) {
                if (!investment.getCryptoCurrency().getName().equals(assetName))
                    newInvestments.add(investment);
            }
            portfolioToRemoveFrom.getInvestments().clear();
            portfolioToRemoveFrom.getInvestments().addAll(newInvestments);
        } else
            System.err.println("Erro: Ativo não encontrado no portfólio ou portfólio inválido.");
        // Reescreve o arquivo, omitindo o ativo removido
    }

    // Método de validação de portfólio
    private boolean isValidPortfolio(Portfolio portfolio) {
        if (portfolio == null)
            return false;
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
}