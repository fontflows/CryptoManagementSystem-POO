package com.cryptomanager.controllers;

import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Client")
public class ClientController{
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService){
        this.clientService = clientService;
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

    @PostMapping("/add")
    public ResponseEntity<String> addClient(@RequestParam String userID, @RequestParam String portfolioID, @RequestParam String password){
        try{
            clientService.addClient(userID, portfolioID, password);
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

    @PostMapping("/edit")
    public ResponseEntity<String> updateClient(@RequestParam String userID, @RequestParam String portfolioID, @RequestParam String password){
        try {
            clientService.updateClient(userID, portfolioID, password);
            return ResponseEntity.ok("Cliente atualizado com sucesso!");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



}