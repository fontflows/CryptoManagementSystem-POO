package com.cryptomanager.repositories;

import com.cryptomanager.models.Portifolio;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository // Assumindo que isso é um repositório Spring
public class PortifolioRepository {
    private static final String FILE_PATH = "portifolio.txt";

    // Método para adicionar um item ao portfólio
    public void adicionarItemPortifolio(Portifolio portifolio) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(portifolio.toString() + "\n");
        }
    }

    // Método para carregar o portfólio do arquivo
    public List<Portifolio> loadPortifolio() throws IOException {
        List<Portifolio> portifolio = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    portifolio.add(new Portifolio(parts[0], Double.parseDouble(parts[1])));
                }
            }
        }
        return portifolio;
    }

    // Método para remover um item do portfólio pelo código
    public void removerItemPortifolio(Portifolio portifolioRemover) throws IOException {
        List<Portifolio> portifolio = loadPortifolio();

        // Filtrar a lista para remover o item especificado
        List<Portifolio> novaLista = new ArrayList<>();
        for (Portifolio item : portifolio) {
            if (!item.equals(portifolioRemover)) {
                novaLista.add(item);
            }
        }

        // Reescrever o arquivo com a nova lista
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portifolio item : novaLista) {
                writer.write(item.toString() + "\n");
            }
        }
    }
}