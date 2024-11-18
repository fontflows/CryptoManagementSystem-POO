package com.cryptomanager.controllers;

import com.cryptomanager.models.Client;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.models.StrategyNames;
import com.cryptomanager.repositories.PortfolioRepository;
import com.cryptomanager.services.ClientService;
import com.cryptomanager.services.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/Client")
public class ClientController{
    private final ClientService clientService;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;

    @Autowired
    public ClientController(ClientService clientService, PortfolioRepository portfolioRepository, PortfolioController portfolioController, PortfolioService portfolioService){
        this.clientService = clientService;
        this.portfolioRepository = portfolioRepository;
        this.portfolioService = portfolioService;
    }

    @GetMapping("/get-all-Clients")
    public List<String> getAllClients() {
        return clientService.getAllClientsToString();
    }

    @GetMapping("/search-by-id")
    public String getClientById(@RequestParam String ClientId){
        return clientService.getClientByClientIDToString(ClientId);
    }

    @PostMapping("/add")
    public String addClient(@RequestParam String UserID, @RequestParam String portfolioID, @RequestParam String password, @RequestParam StrategyNames strategyNames, @RequestParam double balance){
        Portfolio portfolio;

        try {
            portfolio = new Portfolio(portfolioID, UserID, strategyNames.getDisplayName(), balance);
            portfolioService.addPortfolio(portfolio);
            clientService.addClient(new Client(UserID,portfolioRepository.loadPortfolioByUserIdAndPortfolioId(UserID,portfolioID),password));
            return "Cliente adicionado com sucesso";
        }
        catch (IOException e) {
            return String.valueOf(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor ao adicionar Portfolio: " + e.getMessage()));

        } catch (IllegalArgumentException e) {
            return String.valueOf(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entrada inválida: " + e.getMessage()));
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