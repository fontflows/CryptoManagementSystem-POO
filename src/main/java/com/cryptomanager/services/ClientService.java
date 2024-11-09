package com.cryptomanager.services;

import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.models.Client;
import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.repositories.CryptoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ClientService{

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    public List<String> getAllClientsToString(){
        try{
            return clientRepository.loadClientsToString();
        } catch (IOException e) {
            throw new ClientServiceException("Erro ao carregar clientes",e);
        }
    }

    public String getClientByClientIDToString(String ClientID){
        try {
             return clientRepository.loadClientByIDToString(ClientID);
        } catch (IOException e){
            logger.error("Erro ao carregar cliente", e);
            throw new ClientServiceException("Erro ao carregar cliente", e);
        }
    }

    public void addClient(Client client){
        try{
            clientRepository.saveClient(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteClientByClientID(String ClientId){
        try {
            clientRepository.deleteClientByID(ClientId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateClient(Client client){
        try{
            clientRepository.updateClient(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}