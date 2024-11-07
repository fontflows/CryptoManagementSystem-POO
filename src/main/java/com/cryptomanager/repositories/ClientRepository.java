package com.cryptomanager.repositories;

import com.cryptomanager.models.Client;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClientRepository {
    private final PortfolioRepository portfolioRepository;

    private static final String FILE_PATH = "clients.txt";

    public ClientRepository(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public void saveClient(Client client) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(client.toString() + "\n");
        }
    }

    public List<Client> loadClients() throws IOException {
        List <Client> clients = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    clients.add(new Client(parts[0], portfolioRepository.loadPortfolioByUserIdAndPortfolioId(parts[0], parts[1]), parts[2]));
                }
            }
        }
        return clients;
    }
    public Client loadClientByID(String clientID) throws IOException {
        Client client = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if ((parts.length == 3)&&(parts[0].equals(clientID))) {
                    client = (new Client(parts[0], portfolioRepository.loadPortfolioByUserIdAndPortfolioId(parts[0],parts[1]),parts[2]));
                }
            }
        }
        if (client == null) { throw new IllegalArgumentException("Cliente não encontrado"); }
        return client;
    }
    public void deleteClientByID(String clientID) throws IOException { // Tem que verificar se ta tudo vazio *dps faço
        List<Client> clients = loadClients();
        Client removedClient = null;
        for(Client client: clients){
            if(client.getClientID().equals(clientID)){
                removedClient = client;
                break;
            }
        }
        if (removedClient == null) { throw new IllegalArgumentException("Cliente não encontrado"); }
        clients.remove(removedClient);
        // Reescreve o arquivo com a lista atualizada
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Client client : clients) {
                writer.write(client.toString() + "\n");
            }
        }
    }
    public void updateClient(Client searchClient)throws IOException {
        List<Client> clients = loadClients();
        boolean found = false;
        for (Client client : clients) {
            if (client.getClientID().equals(searchClient.getClientID())) {
                found = true;
                break;
            }
        }
        if(found){
            deleteClientByID(searchClient.getClientID());
            saveClient(searchClient);
        }
        else{
            throw new IllegalArgumentException("Cliente não encontrado");
        }
    }
}
