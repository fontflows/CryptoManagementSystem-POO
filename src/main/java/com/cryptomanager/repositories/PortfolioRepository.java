package com.cryptomanager.repositories;

import com.cryptomanager.models.*;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.cryptomanager.services.PortfolioService.hasAsset;

@Repository
public class PortfolioRepository {
    private static final String FILE_PATH = "portfolio.txt";

    // Método para adicionar ou atualizar um portfólio
    public void addPortfolio(Portfolio portfolio) {
        System.out.println("Adicionando portfólio: " + portfolio); // Log para verificar o portfólio
        if (!isValidPortfolio(portfolio)) {
            System.err.println("Erro: Portfólio inválido.");
            return;
        }
        savePortfolio(portfolio);
    }

    // Salvar ou atualizar um portfólio no arquivo
    public void savePortfolio(Portfolio portfolio) {
        List<Portfolio> allPortfolios = loadAllPortfolios();

        Portfolio existingPortfolio = findPortfolio(allPortfolios, portfolio.getId(), portfolio.getUserId());

        if (existingPortfolio != null) {
            updatePortfolio(existingPortfolio, portfolio);
        } else {
            allPortfolios.add(portfolio);
        }

        saveAllPortfolios(allPortfolios);
    }

    // Carregar portfólio por ID e userId
    public Portfolio loadPortfolioByUserIdAndPortfolioId(String userId, String portfolioId) {
        List<Portfolio> allPortfolios = loadAllPortfolios();
        return findPortfolio(allPortfolios, portfolioId, userId);
    }

    // Remover ativo de um portfólio específico
    public void removeAssetFromPortfolio(String portfolioId, String userId, String assetName) {
        Portfolio portfolio = loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);

        if (portfolio != null && hasAsset(assetName, portfolio)) {
            portfolio.getInvestments().removeIf(inv -> inv.getCryptoCurrency().getName().equals(assetName));
            savePortfolio(portfolio);  // Salva as mudanças
        } else {
            System.err.println("Erro: Ativo não encontrado ou portfólio inválido.");
        }
    }

    // Salvar todos os portfólios no arquivo
    private void saveAllPortfolios(List<Portfolio> portfolios) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portfolio portfolio : portfolios) {
                writer.write(portfolio.toString());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar portfólios: " + e.getMessage());
        }
    }

    // Carregar todos os portfólios do arquivo
    private List<Portfolio> loadAllPortfolios() {
        List<Portfolio> portfolioList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            Portfolio currentPortfolio = null;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length == 4) {
                    currentPortfolio = new Portfolio(parts[0], parts[1], new ArrayList<>(), parts[2], Double.parseDouble(parts[3]));
                    portfolioList.add(currentPortfolio);
                } else if (parts.length >= 8 && currentPortfolio != null) {
                    Investment investment = createInvestmentFromParts(parts);
                    currentPortfolio.getInvestments().add(investment);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar portfólios: " + e.getMessage());
        }
        return portfolioList;
    }

    // Função auxiliar para criar um investimento a partir de uma linha de texto
    private Investment createInvestmentFromParts(String[] parts) {
        String cryptoName = parts[0];
        double price = Double.parseDouble(parts[1]);
        double growthRate = Double.parseDouble(parts[2]);
        double marketCap = Double.parseDouble(parts[3]);
        double volume24h = Double.parseDouble(parts[4]);
        int riskFactor = Integer.parseInt(parts[5]);
        double quantity = Double.parseDouble(parts[6]);
        double purchasePrice = Double.parseDouble(parts[7]);

        CryptoCurrency cryptoCurrency = new CryptoCurrency(cryptoName, price, growthRate, marketCap, volume24h, riskFactor);
        return new Investment(cryptoCurrency, purchasePrice, quantity);
    }

    // Encontrar portfólio por ID e userId
    private Portfolio findPortfolio(List<Portfolio> portfolios, String portfolioId, String userId) {
        return portfolios.stream()
                .filter(p -> p.getId().equals(portfolioId) && p.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    // Atualizar portfólio existente com novos investimentos
    private void updatePortfolio(Portfolio existingPortfolio, Portfolio newPortfolio) {
        for (Investment newInvestment : newPortfolio.getInvestments()) {
            Investment existingInvestment = existingPortfolio.getInvestments().stream()
                    .filter(inv -> inv.getCryptoCurrency().getName().equals(newInvestment.getCryptoCurrency().getName()))
                    .findFirst()
                    .orElse(null);

            if (existingInvestment != null) {
                double totalQuantity = existingInvestment.getCryptoInvestedQuantity() + newInvestment.getCryptoInvestedQuantity();
                double averagePrice = (
                        (existingInvestment.getPurchasePrice() * existingInvestment.getCryptoInvestedQuantity()) +
                                (newInvestment.getPurchasePrice() * newInvestment.getCryptoInvestedQuantity())
                ) / totalQuantity;

                existingInvestment.setCryptoInvestedQuantity(totalQuantity);
                existingInvestment.setPurchasePrice(averagePrice);
            } else {
                existingPortfolio.getInvestments().add(newInvestment);
            }
            existingPortfolio.setInvestmentStrategy(newPortfolio.getInvestmentStrategy());
            existingPortfolio.setBalance(newPortfolio.getBalance());
        }
    }

    // Validação de portfólio
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
}
