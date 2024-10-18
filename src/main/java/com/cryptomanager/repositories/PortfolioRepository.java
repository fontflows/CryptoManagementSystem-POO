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

        // Carregamento do portfólio existente
        Portfolio existingPortfolio = loadPortfolioByUserIdAndPortfolioId(portfolio.getUserId(), portfolio.getId());

        // Atualiza ou adiciona investimentos
        for (Investment investment : portfolio.getInvestments()) {
            if (existingPortfolio != null) {
                // Atualização da quantidade e do preço de compra, caso o ativo já exista
                if (existingPortfolio.hasAsset(investment.getCryptoCurrency().getName())) {
                    existingPortfolio.getInvestments().remove(investment);
                }
                existingPortfolio.addAsset(investment.getCryptoCurrency(), investment.getPurchasePrice(),
                        investment.getCryptoInvestedQuantity());
            } else {
                portfolio.addAsset(investment.getCryptoCurrency(), investment.getPurchasePrice(),
                        investment.getCryptoInvestedQuantity());
            }
        }
        // Salvamento de todos os portfólios de volta ao arquivo txt
        savePortfolio(portfolio); // Salva o portfólio atualizado
    }

    // Método para salvar um portfólio no arquivo
    private void savePortfolio(Portfolio portfolio) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(portfolio.getId() + "," + portfolio.getUserId() + "\n");
            for (Investment investment : portfolio.getInvestments()) {
                writer.write(portfolio.getId() + "," + portfolio.getUserId() + "," +
                        investment.getCryptoCurrency().getName() + "," +
                        investment.getCryptoInvestedQuantity() + "," +
                        investment.getPurchasePrice() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar portfólio: " + e.getMessage());
        }
    }

    // Método para carregar um portfólio específico de um usuário
    public Portfolio loadPortfolioByUserIdAndPortfolioId(String userId, String portfolioId) {
        if (userId == null || userId.isEmpty()) {
            System.err.println("Erro: userId não pode ser nulo ou vazio.");
            return null; // Retorna null se o userId for inválido
        }

        if (portfolioId == null || portfolioId.isEmpty()) {
            System.err.println("Erro: portfolioId não pode ser nulo ou vazio.");
            return null; // Retorna null se o portfolioId for inválido
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 5) continue; // Verifica se a linha tem dados suficientes

                String loadedPortfolioId = parts[0];
                String userIdFromFile = parts[1];

                if (userIdFromFile.equals(userId) && loadedPortfolioId.equals(portfolioId)) {
                    String cryptoName = parts[2];
                    double quantity = Double.parseDouble(parts[3]);
                    double purchasePrice = Double.parseDouble(parts[4]);

                    Portfolio portfolio = new Portfolio(loadedPortfolioId, userIdFromFile);
                    CryptoCurrency cryptoCurrency = new CryptoCurrency(cryptoName, purchasePrice);
                    portfolio.addAsset(cryptoCurrency, purchasePrice, quantity);

                    return portfolio; // Retorna o portfólio encontrado
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar portfólio: " + e.getMessage());
        }

        return null; // Retorna null se o portfólio não for encontrado
    }

    // Método para remover um ativo de um portfólio pelo ID e userId
    public void removeAssetFromPortfolio(String portfolioId, String userId, String assetName) {
        if (portfolioId == null || portfolioId.isEmpty() || userId == null || userId.isEmpty()) {
            System.err.println("Erro: portfolioId e/ou userId não podem ser nulos ou vazios.");
            return;
        }

        Portfolio portfolioToRemoveFrom = loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);

        if (portfolioToRemoveFrom != null && portfolioToRemoveFrom.hasAsset(assetName)) {
            List<Investment> newInvestments = new ArrayList<>();
            for (Investment investment : portfolioToRemoveFrom.getInvestments()) {
                if (!investment.getCryptoCurrency().getName().equals(assetName)) {
                    newInvestments.add(investment);
                }
            }
            portfolioToRemoveFrom.getInvestments().clear();
            portfolioToRemoveFrom.getInvestments().addAll(newInvestments);

            // Reescreve o arquivo com todos os portfólios atualizados
            saveAllPortfolios();
        } else {
            System.err.println("Erro: Ativo não encontrado no portfólio ou portfólio inválido.");
        }
    }

    // Método para salvar todos os portfólios no arquivo
    private void saveAllPortfolios() {
        List<Portfolio> allPortfolios = loadAllPortfolios(); // Método para carregar todos os portfólios

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portfolio portfolio : allPortfolios) {
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

    // Método para carregar todos os portfólios
    private List<Portfolio> loadAllPortfolios() {
        List<Portfolio> portfolioList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 5) continue; // Verifica se a linha tem dados suficientes

                String portfolioId = parts[0];
                String userId = parts[1];
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
                    portfolio = new Portfolio(portfolioId, userId);
                    portfolioList.add(portfolio);
                }

                CryptoCurrency cryptoCurrency = new CryptoCurrency(cryptoName, purchasePrice);
                portfolio.addAsset(cryptoCurrency, purchasePrice, quantity);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar portfólios: " + e.getMessage());
        }
        return portfolioList;
    }

    // Método de validação de portfólio
    private boolean isValidPortfolio(Portfolio portfolio) {
        if (portfolio == null) return false;
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