package com.cryptomanager.repositories;

import com.cryptomanager.models.*;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.cryptomanager.repositories.CryptoRepository.loadCryptoByName;

@Repository
public class PortfolioRepository {
    private static final String FILE_PATH = "portfolio.txt";

    // Adiciona um portfólio novo
    public void addPortfolio(Portfolio portfolio) throws IOException {
        if (!isValidPortfolio(portfolio)) {
            throw new IllegalArgumentException("Portfólio não encontrado");
        }
        savePortfolio(portfolio);
    }

    //Adiciona um portfolio novo no arquivo
    private void savePortfolio(Portfolio portfolio) throws IOException{
        if(portfolioExists(portfolio.getId(), portfolio.getUserId())) { throw new IllegalArgumentException("Portfolio com esse ID ja existe"); }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(portfolio.toString());
        }
    }

    // Carrega o portfólio especificado por ID e userId
    public Portfolio loadPortfolioByUserIdAndPortfolioId(String userId, String portfolioId) {
        List<Portfolio> allPortfolios = loadAllPortfolios();
        for(Portfolio portfolio: allPortfolios){
            if(portfolio.getUserId().equalsIgnoreCase(userId.trim()) && portfolio.getId().equalsIgnoreCase(portfolioId.trim())){
                return portfolio;
            }
        }
        throw new NoSuchElementException("Portfolio não encontrado");
    }

    // Atualiza todos os portfólios no arquivo
    public void updatePortfolio(Portfolio updatedPortfolio) throws IOException{
        if(!portfolioExists(updatedPortfolio.getUserId(), updatedPortfolio.getId())) { throw new NoSuchElementException("Portfolio não encontrado"); }
        List<Portfolio> allPortfolios = loadAllPortfolios();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portfolio portfolio : allPortfolios) {
                if(updatedPortfolio.getId().equalsIgnoreCase(portfolio.getId().trim())){
                    writer.write(updatedPortfolio.toString());
                }

                else
                    writer.write(portfolio.toString());
            }
        }
    }

    // Carrega todos os portfólios do arquivo
    public List<Portfolio> loadAllPortfolios() {
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
                }

                else if (parts.length == 3 && currentPortfolio != null) {
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
    private Investment createInvestmentFromParts(String[] parts) throws IOException {
        if (parts.length < 3)
            throw new IllegalArgumentException("Dados do investimento mal formados");

        String cryptoName = parts[0];
        double quantity = Double.parseDouble(parts[1]);
        double purchasePrice = Double.parseDouble(parts[2]);

        CryptoCurrency cryptoCurrency = loadCryptoByName(cryptoName);
        return new Investment(cryptoCurrency, purchasePrice, quantity);
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

        return true;
    }

    public void deletePortfolio(String userID, String portfolioID) throws IOException {
        if(!portfolioExists(userID, portfolioID)) { throw new NoSuchElementException("Portfolio não encontrado"); }
        if(portfolioHasInvestments(userID, portfolioID)) { throw new IllegalArgumentException("Portfolio tem investimentos ativos"); }
        List<Portfolio> portfolios = loadAllPortfolios();
        Portfolio removedPortfolio = null;
        for(Portfolio currentPortfolio: portfolios){
            if(currentPortfolio.getUserId().equalsIgnoreCase(userID.trim()) && currentPortfolio.getId().equalsIgnoreCase(portfolioID.trim())){
                removedPortfolio = currentPortfolio;
                break;
            }
        }
        portfolios.remove(removedPortfolio);
        // Reescreve o arquivo com a lista atualizada
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for(Portfolio currentPortfolio: portfolios){
                writer.write(currentPortfolio.toString() + "\n");
            }
        }
    }

    private boolean portfolioExists(String userID, String portfolioID) throws IOException{
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equalsIgnoreCase(portfolioID.trim()) && parts[1].equalsIgnoreCase(userID.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    //Verifica se um portfolio tem investimentos
    private boolean portfolioHasInvestments(String userID, String portfolioID) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equalsIgnoreCase(portfolioID.trim()) && parts[1].equalsIgnoreCase(userID.trim())) {
                    found = true;
                }
                else if(parts.length >= 8 && found){
                    return true;
                }
                else if(parts.length == 4 && found){
                    return false;
                }
            }
        }
        return false;
    }
}
