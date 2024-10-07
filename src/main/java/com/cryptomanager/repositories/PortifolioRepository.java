package com.cryptomanager.repositories;

import com.cryptomanager.models.Portfolio;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository // Assumindo que isso é um repositório Spring
public class PortifolioRepository {
    private static final String FILE_PATH = "portifolio.txt";

    // Método para adicionar um item ao portfólio
    public void adicionarItemPortifolio(Portfolio portfolio) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(portfolio.toString() + "\n");
        }
    }

    // Método para carregar o portfólio do arquivo
    public List<Portfolio> loadPortifolio() throws IOException {
        List<Portfolio> portfolio = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    portfolio.add(new Portfolio(parts[0], Double.parseDouble(parts[1])));
                }
            }
        }
        return portfolio;
    }

    // Método para remover um item do portfólio pelo código
    public void removerItemPortifolio(Portfolio portfolioRemover) throws IOException {
        List<Portfolio> portfolio = loadPortifolio();

        // Filtrar a lista para remover o item especificado
        List<Portfolio> novaLista = new ArrayList<>();
        for (Portfolio item : portfolio) {
            if (!item.equals(portfolioRemover)) {
                novaLista.add(item);
            }
        }

        // Reescrever o arquivo com a nova lista
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portfolio item : novaLista) {
                writer.write(item.toString() + "\n");
            }
        }
    }
}