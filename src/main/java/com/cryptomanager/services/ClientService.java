package com.cryptomanager.services;

import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.models.Client;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClientService{

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository clientRepository;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, PortfolioRepository portfolioRepository) {
        this.clientRepository = clientRepository;
        this.portfolioRepository = portfolioRepository;
    }

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

    public String getClientByClientIDToString(String clientID){
        try {
             return clientRepository.loadClientByIDToString(clientID);
        } catch (IOException e) {
            logger.error("Erro ao carregar cliente", e);
            throw new ClientServiceException("Erro interno do servidor ao carregar cliente" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao carregar cliente", e);
            throw new ClientServiceException("Erro ao carregar cliente: " + e.getMessage(), e);
        }
    }

    public void addClient(String userID, String portfolioID, String password, String strategyName, double balance){
        try{
            userID = userID.toUpperCase().trim();
            portfolioID = portfolioID.toUpperCase().trim();
            password = password.trim();
            Portfolio portfolio = new Portfolio(portfolioID, userID, strategyName, balance);
            clientRepository.saveClient(new Client(userID, portfolio, password, "CLIENT"));
        } catch (IOException e) {
            throw new ClientServiceException("Erro interno do servidor ao adicionar cliente: " + e.getMessage(), e);
        } catch (IllegalArgumentException e){
            throw new ClientServiceException("Erro ao adicionar cliente: " + e.getMessage(), e);
        }
    }

    public void deleteClientByClientID(String clientID){
        try {
            clientRepository.deleteClientByID(clientID);
        } catch (IOException e) {
            logger.error("Erro ao remover cliente", e);
            throw new ClientServiceException("Erro interno do servidor ao remover cliente" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao remover cliente", e);
            throw new ClientServiceException("Erro ao remover cliente: " + e.getMessage(), e);
        }
    }

    public void updateClient(String userID, String password) {
        try {
            Client client = clientRepository.loadClientByID(userID);
            client.setPassword(password.trim());
            clientRepository.updateClient(client);
        } catch (IOException e) {
            logger.error("Erro ao atualizar cliente", e);
            throw new ClientServiceException("Erro interno do servidor ao atualizar cliente", e);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            logger.error("Erro ao atualizar cliente", e);
            throw new ClientServiceException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }
}