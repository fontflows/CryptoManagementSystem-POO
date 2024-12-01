package com.cryptomanager.controllers;


import com.cryptomanager.models.StrategyNames;
import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.repositories.LoginRepository;
import com.cryptomanager.services.ClientService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("1/client-info")
public class ClientController{
    private final ClientService clientService;
    private final LoginRepository loginRepository;

    @Autowired
    public ClientController(ClientService clientService, LoginRepository loginRepository) {
        this.clientService = clientService;
        this.loginRepository = loginRepository;
    }

    @GetMapping("/get-own-info")
    public ResponseEntity<?> getLoggedClient() {
        try {
            return ResponseEntity.ok(clientService.getClientByClientIDToString(loginRepository.loadLoggedInfo()[0]));
        } catch (ClientServiceException | IOException e) {
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