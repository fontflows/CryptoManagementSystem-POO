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

    public String getClientByClientIDToString(String ClientID){
        try {
             return clientRepository.loadClientByIDToString(ClientID);
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
            userID = userID.toUpperCase();
            portfolioID = portfolioID.toUpperCase();
            Portfolio portfolio = new Portfolio(portfolioID, userID, strategyName, balance);
            portfolioRepository.addPortfolio(portfolio);
            clientRepository.saveClient(new Client(userID, portfolio, password, "CLIENT"));
        } catch (IOException e) {
            throw new ClientServiceException("Erro interno do servidor ao adicionar cliente: " + e.getMessage(), e);
        } catch (IllegalArgumentException | NoSuchElementException e){
            throw new ClientServiceException("Erro ao adicionar cliente: " + e.getMessage(), e);
        }
    }

    public void deleteClientByClientID(String ClientId){
        try {
            clientRepository.deleteClientByID(ClientId);
        } catch (IOException e) {
            logger.error("Erro ao remover cliente", e);
            throw new ClientServiceException("Erro interno do servidor ao remover cliente" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao remover cliente", e);
            throw new ClientServiceException("Erro ao remover cliente: " + e.getMessage(), e);
        }
    }

    public void updateClient(String userID, String portfolioID, String password) {
        try {
            userID = userID.toUpperCase();
            portfolioID = portfolioID.toUpperCase();
            Client client = new Client(userID, portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID), password, "CLIENT");
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