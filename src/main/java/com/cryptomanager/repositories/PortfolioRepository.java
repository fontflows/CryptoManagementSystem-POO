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

    // Funcao para salvar um portfólio no arquivo
    public void savePortfolio(Portfolio portfolio) {
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

    // Funcao para carregar um portfólio específico de um usuário
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
            List<Investment> investments = new ArrayList<>(); // Lista para armazenar os investimentos

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 5) continue; // Verifica se a linha tem dados suficientes

                String loadedPortfolioId = parts[0];
                String userIdFromFile = parts[1];

                if (userIdFromFile.equals(userId) && loadedPortfolioId.equals(portfolioId)) {
                    // Carrega os investimentos
                    String cryptoName = parts[2];
                    double quantity = Double.parseDouble(parts[3]);
                    double purchasePrice = Double.parseDouble(parts[4]);

                    CryptoCurrency cryptoCurrency = new CryptoCurrency(cryptoName, purchasePrice);
                    Investment investment = new Investment(cryptoCurrency, purchasePrice, quantity);
                    investments.add(investment); // Adiciona o investimento à lista

                    // Cria e retorna o portfólio
                    return new Portfolio(loadedPortfolioId, userIdFromFile, investments);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar portfólio: " + e.getMessage());
        }

        return null; // Retorna null se o portfólio não for encontrado
    }

    public boolean hasAsset(String assetName, Portfolio portfolio) { // Verifica se há algum ativo existente
        for (Investment investment : portfolio.getInvestments())
            if (investment.getCryptoCurrency().getName().equals(assetName))
                return true;
        return false;
    }

    // Aqui vai usar quando fizermos a função de delete do txt
    // Funcao para remover um ativo de um portfólio pelo ID e userId
    public void removeAssetFromPortfolio(String portfolioId, String userId, String assetName) {
        if (portfolioId == null || portfolioId.isEmpty() || userId == null || userId.isEmpty()) {
            System.err.println("Erro: portfolioId e/ou userId não podem ser nulos ou vazios.");
            return;
        }

        Portfolio portfolioToRemoveFrom = loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);

        if (portfolioToRemoveFrom != null && hasAsset(assetName, portfolioToRemoveFrom)) {
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

    // Funcao para salvar todos os portfólios no arquivo
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

    // Funcao para carregar todos os portfólios
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

                // Verifica se o portfólio já existe
                Portfolio portfolio = null;
                for (Portfolio p : portfolioList) {
                    if (p.getId().equals(portfolioId)) {
                        portfolio = p;
                        break;
                    }
                }

                // Se o portfólio não existe, cria um novo
                if (portfolio == null) {
                    portfolio = new Portfolio(portfolioId, userId, new ArrayList<>());
                    portfolioList.add(portfolio);
                }

                // Cria o investimento e adiciona ao portfólio
                CryptoCurrency cryptoCurrency = new CryptoCurrency(cryptoName, purchasePrice);
                Investment investment = new Investment(cryptoCurrency, purchasePrice, quantity);
                portfolio.getInvestments().add(investment);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar portfólios: " + e.getMessage());
        }
        return portfolioList;
    }

    // Funcao de validação de portfólio
    public boolean isValidPortfolio(Portfolio portfolio) {
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

    public static void addAsset(CryptoCurrency cryptoCurrency, double purchasePrice, double cryptoInvestedQuantity, Portfolio portfolioToAdd) { /* Adição
    de ativos*/
        Investment existingInvestment = null;
        List<Investment> investmentsActual = portfolioToAdd.getInvestments();

        for (Investment investment : investmentsActual) {
            if (investment.getCryptoCurrency().getName().equals(cryptoCurrency.getName())) {
                existingInvestment = investment;
                break;
            }
        }

        if (existingInvestment != null) {
            // Atualiza o investimento existente, além de ter lógica de preço médio.
            double totalQuantity = existingInvestment.getCryptoInvestedQuantity() + cryptoInvestedQuantity;
            double totalValue = (existingInvestment.getPurchasePrice() * existingInvestment.getCryptoInvestedQuantity())
                    + (purchasePrice * cryptoInvestedQuantity);
            double averagePrice = totalValue / totalQuantity;

            // Atualiza o investimento
            existingInvestment.setCryptoInvestedQuantity(totalQuantity);
            existingInvestment.setPurchasePrice(averagePrice);
        } else
            // Adiciona um novo investimento
            investmentsActual.add(new Investment(cryptoCurrency, purchasePrice, cryptoInvestedQuantity));
    }
}
