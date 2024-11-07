package com.cryptomanager.controllers;

import com.cryptomanager.models.Client;
import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Client")
public class ClientController{
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/search-by-id")
    public Client getClientById(String ClientId){
        return clientService.getClientByClientID(ClientId);
    }

    @PostMapping("/add")
    public String addClient(@RequestBody Client client){
        try{
            clientService.addClient(client);
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
    public String updateClient(@RequestBody Client client){
        clientService.updateClient(client);
        return "Cliente editado com sucesso!";
    }



}