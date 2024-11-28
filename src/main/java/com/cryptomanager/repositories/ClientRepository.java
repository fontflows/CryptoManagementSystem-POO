package com.cryptomanager.repositories;

import com.cryptomanager.models.Client;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class ClientRepository {
    private final PortfolioRepository portfolioRepository;

    private static final String FILE_PATH = "clients.txt";

    public ClientRepository(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public void saveClient(Client client) throws IOException {
        if(clientExists(client.getClientID())) { throw new IllegalArgumentException("Cliente com esse userID ja está cadastrado"); }
        portfolioRepository.addPortfolio(client.getPortfolio());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(client + "\n");
        }
    }

    public List<Client> loadClients() throws IOException {
        List <Client> clients = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    clients.add(new Client(parts[0], portfolioRepository.loadPortfolioByUserIdAndPortfolioId(parts[0],parts[1]),parts[2], parts[3]));
                }
            }
        }
        if (clients.isEmpty()) { throw new NoSuchElementException("Nenhum cliente encontrado"); }
        return clients;
    }

    public List<String> loadClientsToString() throws IOException{
        List<Client> clients = loadClients();
        List<String> stringOut = new ArrayList<>();
        for(Client client: clients){
            stringOut.add(client.toString());
        }
        return stringOut;
    }

    public Client loadClientByID(String clientID) throws IOException {
        Client client = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if ((parts.length == 3)&&(parts[0].equalsIgnoreCase(clientID.trim()))) {
                    client = (new Client(parts[0], portfolioRepository.loadPortfolioByUserIdAndPortfolioId(parts[0],parts[1]),parts[2]));
                }
            }
        }
        if (client == null) { throw new NoSuchElementException("Cliente não encontrado"); }
        return client;
    }

    public String loadClientByIDToString(String clientID) throws IOException{
        Client client = loadClientByID(clientID);
        return client.toString();
    }

    public void deleteClientByID(String clientID) throws IOException { // Tem que verificar se ta tudo vazio *dps faço
        if(!clientExists(clientID)) { throw new NoSuchElementException("Cliente não encontrado"); }
        List<Client> clients = loadClients();
        Client removedClient = null;
        for(Client client: clients){
            if(client.getClientID().equalsIgnoreCase(clientID.trim())){
                removedClient = client;
                break;
            }
        }
        assert removedClient != null;
        portfolioRepository.deletePortfolio(clientID, removedClient.getPortfolio().getId());
        clients.remove(removedClient);
        // Reescreve o arquivo com a lista atualizada
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Client client : clients) {
                writer.write(client.toString() + "\n");
            }
        }
    }

    public void updateClient(Client updatedClient) throws IOException {
        if(updatedClient == null || updatedClient.getClientID() == null ) { throw new IllegalArgumentException("Cliente inválido");}
        if(!clientExists(updatedClient.getClientID())) { throw new NoSuchElementException("Cliente não encontrado"); }
        List<Client> allClients = loadClients();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Client client : allClients) {
                if(client.getClientID().equalsIgnoreCase(updatedClient.getClientID().trim())) {
                    writer.write(updatedClient.toString());
                    writer.newLine();
                }
                else {
                    writer.write(client.toString());
                    writer.newLine();
                }
            }
        }
    }

    private boolean clientExists(String clientID) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if ((parts.length == 3) && (parts[0].equalsIgnoreCase(clientID.trim()))) {
                    return true;
                }
            }
        }
        return false;
    }

}
