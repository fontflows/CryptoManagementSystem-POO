package com.cryptomanager.repositories;

import com.cryptomanager.models.*;
import com.cryptomanager.exceptions.*;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.cryptomanager.repositories.CryptoRepository.loadCryptoByName;

@Repository
public class PortfolioRepository {
    private static final String FILE_PATH = "portfolio.txt";

    // Adiciona um portfólio novo
    public void addPortfolio(Portfolio portfolio) throws IOException {
        if (!isValidPortfolio(portfolio)) {
            throw new PortfolioValidationException("Portfólio não válido.");
        }

        savePortfolio(portfolio);
    }

    // Adiciona um portfólio no arquivo
    private void savePortfolio(Portfolio portfolio) throws IOException {
        if (portfolioExists(portfolio.getId(), portfolio.getUserId())) {
            throw new PortfolioAlreadyExistsException("Portfólio com esse ID já existe para o usuário: " + portfolio.getUserId());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(portfolio + "\n");
        }
    }

    // Carrega o portfólio especificado por ID e userId
    public Portfolio loadPortfolioByUserIdAndPortfolioId(String userId, String portfolioId) {
        List<Portfolio> allPortfolios = loadAllPortfolios();

        for (Portfolio portfolio : allPortfolios) {
            if (portfolio.getUserId().equalsIgnoreCase(userId.trim()) && portfolio.getId().equalsIgnoreCase(portfolioId.trim()))
                return portfolio;
        }

        // Exceção lançada com causa
        throw new PortfolioNotFoundException("Portfólio não encontrado para o usuário: " + userId + " e ID: " + portfolioId, null);
    }

    // Atualiza todos os portfólios no arquivo
    public void updatePortfolio(Portfolio updatedPortfolio) throws IOException {
        if (!portfolioExists(updatedPortfolio.getUserId(), updatedPortfolio.getId())) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + updatedPortfolio.getId(), null);
        }

        List<Portfolio> allPortfolios = loadAllPortfolios();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portfolio portfolio : allPortfolios) {
                if (updatedPortfolio.getId().equalsIgnoreCase(portfolio.getId().trim()))
                    writer.write(updatedPortfolio + "\n");
                else
                    writer.write(portfolio + "\n");
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
                } else if (parts.length == 3 && currentPortfolio != null) {
                    Investment investment = createInvestmentFromParts(parts);
                    currentPortfolio.getInvestments().add(investment);
                }
            }
        } catch (IOException e) {
            throw new PortfolioLoadException("Erro ao carregar portfólios: " + e.getMessage(), e);
        }
        return portfolioList;
    }

    // Função auxiliar para criar um investimento a partir de uma linha de texto
    private Investment createInvestmentFromParts(String[] parts) throws IOException {
        if (parts.length < 3) {
            throw new IllegalArgumentException("Dados do investimento mal formados");
        }

        String cryptoName = parts[0];
        double quantity = Double.parseDouble(parts[1]);
        double purchasePrice = Double.parseDouble(parts[2]);

        CryptoCurrency cryptoCurrency = loadCryptoByName(cryptoName);
        return new Investment(cryptoCurrency, purchasePrice, quantity);
    }

    // Validação de portfólio
    public boolean isValidPortfolio(Portfolio portfolio) {
        if (portfolio == null) {
            throw new PortfolioValidationException("Portfólio não pode ser nulo.");
        }

        if (portfolio.getUserId() == null || portfolio.getUserId().isEmpty()) {
            throw new PortfolioValidationException("userId não pode ser nulo ou vazio.");
        }

        if (portfolio.getId() == null || portfolio.getId().isEmpty()) {
            throw new PortfolioValidationException("portfolioId não pode ser nulo ou vazio.");
        }

        return true;
    }

    public void deletePortfolio(String userID, String portfolioID) throws IOException {
        if (!portfolioExists(userID, portfolioID)) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + portfolioID, null);
        }

        if (portfolioHasInvestments(userID, portfolioID)) {
            throw new PortfolioHasInvestmentsException("Portfólio tem investimentos ativos, não pode ser excluído.");
        }

        List<Portfolio> portfolios = loadAllPortfolios();
        Portfolio removedPortfolio = null;

        for (Portfolio currentPortfolio : portfolios) {
            if (currentPortfolio.getUserId().equalsIgnoreCase(userID.trim()) && currentPortfolio.getId().equalsIgnoreCase(portfolioID.trim())) {
                removedPortfolio = currentPortfolio;
                break;
            }
        }

        portfolios.remove(removedPortfolio);

        // Reescreve o arquivo com a lista atualizada
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portfolio currentPortfolio : portfolios) {
                writer.write(currentPortfolio.toString() + "\n");
            }
        }
    }

    private boolean portfolioExists(String userID, String portfolioID) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");

                if (parts.length == 4 && parts[0].equalsIgnoreCase(portfolioID.trim()) && parts[1].equalsIgnoreCase(userID.trim()))
                    return true;
            }
        }

        return false;
    }

    // Verifica se um portfólio tem investimentos
    private boolean portfolioHasInvestments(String userID, String portfolioID) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");

                if (parts.length == 4 && parts[0].equalsIgnoreCase(portfolioID.trim()) && parts[1].equalsIgnoreCase(userID.trim())) {
                    found = true;
                } else if (parts.length >= 8 && found) {
                    return true;
                } else if (parts.length == 4 && found) {
                    return false;
                }
            }
        }

        return false;
    }
}