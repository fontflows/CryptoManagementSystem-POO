package com.cryptomanager.controllers;


import com.cryptomanager.models.StrategyNames;
import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.repositories.LoginRepository;
import com.cryptomanager.services.ClientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/client")
public class ClientController{
    private final ClientService clientService;
    private final LoginRepository loginRepository;

    @Autowired
    public ClientController(ClientService clientService, LoginRepository loginRepository) {
        this.clientService = clientService;
        this.loginRepository = loginRepository;
    }

    @GetMapping("/get-all-Clients")
    public ResponseEntity<?> getAllClients() {
        try {
            return ResponseEntity.ok(clientService.getAllClientsToString());
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search-by-id")
    public ResponseEntity<?> getClientByID(String userID) {
        try {
            return ResponseEntity.ok(clientService.getClientByClientIDToString(userID));
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/get-own-info")
    public ResponseEntity<?> getLoggedClient() {
        try {
            return ResponseEntity.ok(clientService.getClientByClientIDToString(loginRepository.loadLoggedInfo()[0]));
        } catch (ClientServiceException | IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
  
    @PostMapping("/add")
    public ResponseEntity<String> addClient(@RequestParam String userID, @RequestParam String portfolioID, @RequestParam String password, @RequestParam StrategyNames strategyNames){
        try{
            clientService.addClient(userID, portfolioID, password, strategyNames.getDisplayName(), 0);
            return ResponseEntity.ok("Cliente cadastrado com sucesso");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteClient(@RequestParam String userID) {
        try {
            clientService.deleteClientByClientID(userID);
            return ResponseEntity.ok("Cliente removido com sucesso!");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/edit-passwords")
    public ResponseEntity<String> updateClient(@RequestParam String userID, @RequestParam String password){
        try {
            clientService.updateClient(userID, password);
            return ResponseEntity.ok("Senha atualizada com sucesso!");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/edit-own-password")
    public ResponseEntity<String> updateLoggedClientPassword(@RequestParam String newPassword){
        try {
            clientService.updateClient(loginRepository.loadLoggedInfo()[0], newPassword);
            return ResponseEntity.ok("Senha atualizada com sucesso!");
        } catch (ClientServiceException | IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}