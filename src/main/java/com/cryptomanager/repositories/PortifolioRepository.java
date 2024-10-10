package com.cryptomanager.repositories;

import com.cryptomanager.models.Portfolio;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PortifolioRepository {
    private static final String FILE_PATH = "portifolio.txt";

    // Método para adicionar um portfólio
    public void adicionarPortifolio(Portfolio portfolio) {
        if (!isValidPortfolio(portfolio)) {
            System.err.println("Erro: Portfólio inválido.");
            return; // Retorna se o portfólio não for válido
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(portfolio.getUserId() + "\n");
            writer.write(portfolio.toString() + "\n");
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Erro ao adicionar portfólio: " + e.getMessage());
        }
    }

    // Método para carregar o portfólio de um usuário específico
    public List<Portfolio> loadPortifolioByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            System.err.println("Erro: userId não pode ser nulo ou vazio.");
            return new ArrayList<>();
        }

        List<Portfolio> portfolioList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String currentUserId = null;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                if (!line.contains(",")) {
                    currentUserId = line;
                } else if (currentUserId != null && currentUserId.equals(userId)) {
                    String[] parts = line.split(",");
                    if (parts.length < 1) continue;

                    Portfolio portfolio = new Portfolio(parts[0], currentUserId);
                    for (int i = 1; i < parts.length; i += 2) {
                        try {
                            String assetName = parts[i];
                            double amount = Double.parseDouble(parts[i + 1]);
                            if (amount < 0) {
                                System.err.println("Erro: Quantidade de ativo não pode ser negativa.");
                                continue; // Ignora ativos com quantidade negativa
                            }
                            portfolio.addAsset(assetName, amount);
                        } catch (NumberFormatException e) {
                            System.err.println("Erro ao converter o valor de quantidade para um número: " + e.getMessage());
                        }
                    }
                    portfolioList.add(portfolio);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar portfólio: " + e.getMessage());
        }
        return portfolioList;
    }

    // Método para remover um portfólio pelo ID e userId
    public void removerPortifolio(String portfolioId, String userId) {
        if (portfolioId == null || portfolioId.trim().isEmpty() || userId == null || userId.trim().isEmpty()) {
            System.err.println("Erro: portfolioId e userId não podem ser nulos ou vazios.");
            return;
        }

        List<Portfolio> portfolio = loadPortifolioByUserId(userId);
        List<Portfolio> novaLista = new ArrayList<>();

        for (Portfolio item : portfolio) {
            if (!item.getId().equals(portfolioId)) {
                novaLista.add(item);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            String currentUserId = null;
            for (Portfolio item : novaLista) {
                if (!item.getUserId().equals(currentUserId)) {
                    currentUserId = item.getUserId();
                    writer.write(currentUserId + "\n");
                }
                writer.write(item.toString() + "\n");
                writer.write("\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao remover portfólio: " + e.getMessage());
        }
    }

    // Método de validação de portfólio
    private boolean isValidPortfolio(Portfolio portfolio) {
        if (portfolio == null) {
            return false;
        }
        if (portfolio.getUserId() == null || portfolio.getUserId().trim().isEmpty()) {
            System.err.println("Erro: userId não pode ser nulo ou vazio.");
            return false;
        }
        if (portfolio.getId() == null || portfolio.getId().trim().isEmpty()) {
            System.err.println("Erro: portfolioId não pode ser nulo ou vazio.");
            return false;
        }
        if (portfolio.getAssets() == null || portfolio.getAssets().isEmpty()) {
            System.err.println("Erro: Portfólio deve conter pelo menos um ativo.");
            return false;
        }
        return true;
    }
}