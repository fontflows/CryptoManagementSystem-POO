package com.cryptomanager.repositories;

import com.cryptomanager.models.*;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.cryptomanager.services.PortfolioService.findInvestment;
import static com.cryptomanager.services.PortfolioService.hasAsset;

@Repository
public class PortfolioRepository {
    private static final String FILE_PATH = "portfolio.txt";

    // Adicionar um portfólio novo
    public void addPortfolio(Portfolio portfolio) throws IOException {
        if (!isValidPortfolio(portfolio)) {
            throw new IllegalArgumentException("Portfólio não encontrado");
        }
        savePortfolio(portfolio);
    }

    //Adicionar um portfolio novo no arquivo
    private void savePortfolio(Portfolio portfolio) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(portfolio.toString());
        }
    }

    // Carregar portfólio por ID e userId
    public Portfolio loadPortfolioByUserIdAndPortfolioId(String userId, String portfolioId) {
        List<Portfolio> allPortfolios = loadAllPortfolios();
        return findPortfolio(allPortfolios, portfolioId, userId);
    }

    // Remover ativo de um portfólio específico
    public void removeAssetFromPortfolio(String portfolioId, String userId, String assetName) {
        try {
            Portfolio portfolio = loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);
            if (hasAsset(assetName, portfolio)) {
                Investment removedInvestment = findInvestment(portfolio, assetName);
                portfolio.getInvestments().remove(removedInvestment);
                updatePortfolio(portfolio);  // Salva as mudanças
            } else {
                throw new IllegalArgumentException("Criptomoeda não encontrada no portfólio");
            }
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Erro ao remover ativo: " + e.getMessage());
        }
    }

    // Atualizar todos os portfólios no arquivo
    public void updatePortfolio(Portfolio updatedPortfolio) {
        List<Portfolio> allPortfolios = loadAllPortfolios();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portfolio portfolio : allPortfolios) {
                if(updatedPortfolio.getId().equals(portfolio.getId())){
                    writer.write(updatedPortfolio.toString());
                }
                else {
                    writer.write(portfolio.toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao atualizar portfólios: " + e.getMessage());
        }
    }

    // Carrega todos os portfólios do arquivo
    private List<Portfolio> loadAllPortfolios() {
        List<Portfolio> portfolioList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            Portfolio currentPortfolio = null;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length == 4) {
                    currentPortfolio = new Portfolio(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
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
        for(Portfolio portfolio: portfolios){
            if(portfolio.getUserId().equals(userId) && portfolio.getId().equals(portfolioId)){
                return portfolio;
            }
        }
        throw new IllegalArgumentException("Portfolio não encontrado");
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
