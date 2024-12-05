package com.cryptomanager.services;

import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.exceptions.PortfolioHasInvestmentsException;
import com.cryptomanager.models.Client;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

/** Classe responsavel pelos metodos Service para manipulacao de clientes*/
@Service
public class ClientService{
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository clientRepository;

    /** Construtor ClientService
     * @param clientRepository Instancia que conecta o Service a classe que manipula os dados dos clientes no arquivo.
     */
    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /** Metodo responsavel por obter as informacoes formatadas de todos os clientes.
     * @return Retorna uma lista com as informacoes de todos os clientes de modo formatado.
     * @throws ClientServiceException Caso ocorra algum erro no processo de obter as informacoes dos clientes.
     */
    public List<String> getAllClientsToString(){
        try{
            return clientRepository.loadClientsToString();
        } catch (IOException e) {
            logger.error("Erro ao carregar clientes", e);
            throw new ClientServiceException("Erro interno do servidor ao carregar clientes" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao carregar clientes", e);
            throw new ClientServiceException("Erro ao carregar clientes: " + e.getMessage(), e);
        }
    }

    /** Metodo responsavel por obter as informacoes formatadas de um cliente especifico.
     * @param clientID Recebe o identificador do cliente especifico.
     * @return Retorna uma lista com as informacoes de um cliente especifico de modo formatado.
     * @throws ClientServiceException Caso ocorra algum erro no processo de obter as informacoes do cliente.
     */
    public String getClientByClientIDToString(String clientID){
        try {
             return "| UserID | PortfolioID | Password | Role |\n\n" + clientRepository.loadClientByIDToString(clientID);
        } catch (IOException e) {
            logger.error("Erro ao carregar cliente", e);
            throw new ClientServiceException("Erro interno do servidor ao carregar cliente" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao carregar cliente", e);
            throw new ClientServiceException("Erro ao carregar cliente: " + e.getMessage(), e);
        }
    }

    /** Metodo responsavel por adicionar novos clientes no sistema.
     * @param userID Recebe o userID no novo cliente.
     * @param portfolioID Recebe o portfolioID do novo cliente.
     * @param password Recebe a senha do novo cliente.
     * @param strategyName Recebe a estrategia de investimento do novo cliente.
     * @param balance Recebe o saldo do novo cliente.
     * @param role Recebe o Role do novo cliente.
     * @throws ClientServiceException Caso ocorra algum erro no processo de adicionar o cliente.
     */
    public void addClient(String userID, String portfolioID, String password, String strategyName, double balance, String role){
        try{
            userID = userID.toUpperCase().trim();
            portfolioID = portfolioID.toUpperCase().trim();
            password = password.trim();
            Portfolio portfolio = new Portfolio(portfolioID, userID, strategyName, balance);
            clientRepository.saveClient(new Client(userID, portfolio, password, role));
        } catch (IOException e) {
            throw new ClientServiceException("Erro interno do servidor ao adicionar cliente: " + e.getMessage(), e);
        } catch (IllegalArgumentException e){
            throw new ClientServiceException("Erro ao adicionar cliente: " + e.getMessage(), e);
        }
    }

    /** Metodo responsavel por remover um cliente do sistema.
     * @param clientID Recebe o identificador do cliente que sera removido.
     * @throws ClientServiceException Caso ocorra algum erro no processo de remover o cliente.
     */
    public void deleteClientByClientID(String clientID){
        try {
            clientRepository.deleteClientByID(clientID);
        } catch (IOException e) {
            logger.error("Erro ao remover cliente", e);
            throw new ClientServiceException("Erro interno do servidor ao remover cliente" , e);
        } catch (NoSuchElementException | PortfolioHasInvestmentsException e){
            logger.error("Erro ao remover cliente", e);
            throw new ClientServiceException("Erro ao remover cliente: " + e.getMessage(), e);
        }
    }

    /** Metodo responsavel por atualizar a senha de um cliente.
     * @param userID Recebe o userID do usuario que esta alterando a senha.
     * @param password Recebe a nova senha do usuario.
     * @throws ClientServiceException Caso ocorra algum erro no processo de atualizar a senha do usuario.
     */
    public void updateClientPassword(String userID, String password) {
        try {
            Client client = clientRepository.loadClientByID(userID);
            client.setPassword(password.trim());
            clientRepository.updateClient(client);
        } catch (IOException e) {
            logger.error("Erro interno do servidor ao atualizar cliente", e);
            throw new ClientServiceException("Erro interno do servidor ao atualizar cliente", e);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            logger.error("Erro ao atualizar cliente", e);
            throw new ClientServiceException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    /** Metodo responsavel por editar o Role de um usuario.
     * @param userID Recebe o userID do usuario associado.
     * @param role Recebe o novo Role do usuario.
     * @throws ClientServiceException Caso ocorra algum erro no processo de atualizar o Role do usuario.
     */
    public void updateUserRole(String userID, String role){
        try {
            Client client = clientRepository.loadClientByID(userID);
            client.setRole(role);
            clientRepository.updateClient(client);
        } catch (IOException e) {
            logger.error("Erro interno do servidor ao atualizar cliente", e);
            throw new ClientServiceException("Erro interno do servidor ao atualizar cliente", e);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            logger.error("Erro ao atualizar cliente", e);
            throw new ClientServiceException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    /** Metodo responsavel por obter uma lista dos usuarios com Role 'UNAUTHORIZED'.
     * @return Retorna uma lista com todos os usuarios cadastrados no sistema com Role 'UNAUTHORIZED'.
     * @throws ClientServiceException Caso ocorra algum erro no processo de obter a lista de usuarios.
     */
    public String getUnauthorizedClients(){
        try {
            int i = 1;
            List<Client> clients = clientRepository.loadClients();
            StringBuilder unauthorizedClients = new StringBuilder();
            for(Client client : clients){
                if(client.getRole().equalsIgnoreCase("UNAUTHORIZED")){
                    unauthorizedClients.append(i).append(". ").append(client).append('\n');
                    i++;
                }
            }
            if(unauthorizedClients.isEmpty()){
                throw new NoSuchElementException("Nenhum cliente com Role 'UNAUTHORIZED'");
            }
            return "| UserID | PortfolioID | Password | Role |\n\n" + unauthorizedClients;
        } catch (IOException e){
            logger.error("Erro interno do servidor ao carregar clientes", e);
            throw new ClientServiceException("Erro interno do servidor ao carregar clientes", e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao carregar clientes", e);
            throw new ClientServiceException("Erro ao carregar clientes: " + e.getMessage(), e);
        }

    }
}