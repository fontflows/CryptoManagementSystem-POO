package com.cryptomanager.services;

import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.exceptions.CryptoServiceException;
import com.cryptomanager.models.Client;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
            throw new CryptoServiceException("Erro interno do servidor ao carregar clientes" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao carregar clientes", e);
            throw new CryptoServiceException("Erro ao carregar clientes: " + e.getMessage(), e);
        }
    }

    public String getClientByClientIDToString(String ClientID){
        try {
             return clientRepository.loadClientByIDToString(ClientID);
        } catch (IOException e) {
            logger.error("Erro ao carregar cliente", e);
            throw new CryptoServiceException("Erro interno do servidor ao carregar cliente" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao carregar cliente", e);
            throw new CryptoServiceException("Erro ao carregar cliente: " + e.getMessage(), e);
        }
    }

    public void addClient(String userID, String portfolioID, String password){
        try{
            clientRepository.saveClient(new Client(userID,portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID),password));
        } catch (IOException e) {
            throw new ClientServiceException("Erro interno do servidor ao adicionar cliente: " + e.getMessage(), e);
        } catch (IllegalArgumentException e){
            throw new ClientServiceException("Erro ao adicionar cliente: " + e.getMessage(), e);
        }
    }

    public void deleteClientByClientID(String ClientId){
        try {
            clientRepository.deleteClientByID(ClientId);
        } catch (IOException e) {
            logger.error("Erro ao remover cliente", e);
            throw new CryptoServiceException("Erro interno do servidor ao remover cliente" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao remover cliente", e);
            throw new CryptoServiceException("Erro ao remover cliente: " + e.getMessage(), e);
        }
    }

    public void updateClient(String userID, String portfolioID, String password){
        try{
            Client client = new Client(userID,portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID),password);
            clientRepository.updateClient(client);
        } catch (IOException e) {
            logger.error("Erro ao atualizar cliente", e);
            throw new CryptoServiceException("Erro interno do servidor ao atualizar cliente" , e);
        } catch (IllegalArgumentException e){
            logger.error("Erro ao atualizar criptomoeda", e);
            throw new CryptoServiceException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }
}