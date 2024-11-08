package com.cryptomanager.controllers;

import com.cryptomanager.models.Client;
import com.cryptomanager.repositories.PortfolioRepository;
import com.cryptomanager.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Client")
public class ClientController{
    private final ClientService clientService;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public ClientController(ClientService clientService, PortfolioRepository portfolioRepository){
        this.clientService = clientService;
        this.portfolioRepository = portfolioRepository;
    }

    @GetMapping("/get-all-Clients")
    public List<String> getAllClients() {
        return clientService.getAllClientsToString();
    }

    @GetMapping("/search-by-id")
    public Client getClientById(@RequestParam String ClientId){
        return clientService.getClientByClientID(ClientId);
    }

    @PostMapping("/add")
    public String addClient(@RequestParam String UserID, @RequestParam String portfolioID, @RequestParam String password){
        try{
            clientService.addClient(new Client(UserID,portfolioRepository.loadPortfolioByUserIdAndPortfolioId(UserID,portfolioID),password));
            return "Cliente adicionado com sucesso";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteClient(@RequestParam String ClientId){
        try{
            clientService.deleteClientByClientID(ClientId);
            return "Cliente removido com sucesso";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/edit")
    public String updateClient(@RequestParam String UserID, @RequestParam String portfolioID, @RequestParam String password){
        clientService.updateClient(new Client(UserID,portfolioRepository.loadPortfolioByUserIdAndPortfolioId(UserID,portfolioID),password));
        return "Cliente editado com sucesso!";
    }



}