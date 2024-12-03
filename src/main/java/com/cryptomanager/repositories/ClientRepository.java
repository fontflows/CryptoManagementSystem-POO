package com.cryptomanager.repositories;

import com.cryptomanager.models.Client;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Classe responsavel por lidar com a pertinencia de dados dos clientes/usuarios do sistema.
 */
@Repository
public class ClientRepository {
    private final PortfolioRepository portfolioRepository;
    private static final String FILE_PATH = "clients.txt";

    /** Construtor padrao da classe ClientRepository.
     * @param portfolioRepository Instancia da classe que lida com o armazenamento do portfolio (no modelo txt).
     */
    public ClientRepository(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    /** Metodo responsavel por salvar/cadastrar o cliente no sistema.
     * @param client Instancia que recebe o cliente especificado.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida no cadastro do cliente.
     * @throws IllegalArgumentException Excecao lancada, caso o argumento informado para o metodo seja invalido.
     */
    public void saveClient(Client client) throws IOException {
        if(clientExists(client.getClientID())) { throw new IllegalArgumentException("Cliente com esse userID ja está cadastrado"); }

        portfolioRepository.addPortfolio(client.getPortfolio());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(client + "\n");
        }
    }

    /** Metodo responsavel por carregar a lista de clientes cadastrados no sistema.
     * @return Retorna a lista de clientes cadastrados encontrada.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida no carregamento da lista de clientes.
     * @throws NoSuchElementException Excecao lancada, caso o elemento detectado nao exista para o sistema.
     */
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

    /** Metodo responsavel por carregar e formatar a lista de clientes cadastrados no sistema.
     * @return Retorna uma lista de Strings dos clientes cadastrados.
     * @throws IOException Excecao lancada, caso ocorra algume erro de entrada/saida na formatacao da lista de clientes.
     */
    public List<String> loadClientsToString() throws IOException{
        List<Client> clients = loadClients();
        List<String> stringOut = new ArrayList<>();

        for(Client client: clients)
            stringOut.add(client.toString());

        return stringOut;
    }

    /** Metodo responsavel por carregar os clientes, a partir do ID informado no sistema.
     * @param clientID Recebe o ID do cliente informado.
     * @return Retorna a instancia do cliente associada ao ID informado.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida no carregamento do cliente especificado pelo ID.
     * @throws NoSuchElementException Excecao lancada, caso o elemento detectado nao exista para o sistema.
     */
    public Client loadClientByID(String clientID) throws IOException {
        Client client = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if ((parts.length == 4)&&(parts[0].equalsIgnoreCase(clientID.trim()))) {
                    client = (new Client(parts[0], portfolioRepository.loadPortfolioByUserIdAndPortfolioId(parts[0],parts[1]),parts[2], parts[3]));
                }
            }
        }

        if (client == null) { throw new NoSuchElementException("Cliente não encontrado"); }

        return client;
    }

    /** Metodo responsavel por carregar e formatar a instancia de Client, conforme o ID do cliente associado informado no sistema.
     * @param clientID Recebe o ID do cliente informado.
     * @return Retorna a instancia de Client devidamente formatada.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a formatacao da instancia de Client.
     */
    public String loadClientByIDToString(String clientID) throws IOException{
        Client client = loadClientByID(clientID);
        return client.toString();
    }

    /** Metodo responsavel por remover o cliente do arquivo, considerando o seu ID informado no sistema.
     * @param clientID Recebe o ID do cliente iformado.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a remocao do cliente no sistema txt.
     * @throws NoSuchElementException Excecao lancada, caso o elemento detectado nao exista para o sistema.
     */
    public void deleteClientByID(String clientID) throws IOException { // Tem que verificar se ta tudo vazio *dps faço
        if(!clientExists(clientID)) { throw new NoSuchElementException("Cliente não encontrado"); }

        List<Client> clients = loadClients();

        for(Client client: clients){
            if(client.getClientID().equalsIgnoreCase(clientID.trim())){
              portfolioRepository.deletePortfolio(clientID, client.getPortfolio().getId());
              clients.remove(client);
              break;
            }
        }
        // Reescreve o arquivo com a lista atualizada
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Client client : clients)
                writer.write(client.toString() + "\n");
        }
    }

    /** Metodo responsavel por atualizar a instancia de Client, considerando as suas informacoes previas postas no sistema.
     * @param updatedClient Instancia que recebe o cliente a ser atualizado.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a atualizacao da instancia associada ao cliente.
     */
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

    /** Metodo responsavel por averiguar/verificar se um cliente esta cadastrado no sistema e devidamente armazenado no arquivo txt "clients.txt".
     * @param clientID Recebe o ID do cliente informado.
     * @return Retorna o valor booleano da verificacao (verdadeiro ou falso).
     * @throws IOException Excecao lancadda, caso ocorra algum erro de entrada/saida durante a verificacao.
     */
    private boolean clientExists(String clientID) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if ((parts.length == 4) && (parts[0].equalsIgnoreCase(clientID.trim()))) {
                    return true;
                }
            }
        }
        return false;
    }
}