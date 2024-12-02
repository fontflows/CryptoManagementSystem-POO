package com.cryptomanager.controllers;

import com.cryptomanager.models.StrategyNames;
import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Classe responsavel por facilitar a checagem dos usuarios pelo administrador do sistema Swagger
 * */
@RestController
@RequestMapping("/Client")
public class ClientController{
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }

    /** Metodo responsavel por obter todos os clientes cadastrados no sistema (ate o momento no qual a funcao eh chamada).
     * @return Mensagem de retorno da correta execucao das funcoes associadas a obtencao de todos os clientes cadastrados no sistema.
     */
    @GetMapping("/get-all-Clients")
    public ResponseEntity<?> getAllClients() {
        try {
            return ResponseEntity.ok(clientService.getAllClientsToString());
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel por pesquisar o usuario de interesse, conforme o seu id informado.
     * @param userID Recebe o Id do usuario associado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a busca de um cliente, conforme o seu Id.
     */
    @GetMapping("/search-by-id")
    public ResponseEntity<?> getClientByID(String userID) {
        try {
            return ResponseEntity.ok(clientService.getClientByClientIDToString(userID));
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel por adicionar clientes no sistema Swagger.
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @param password Recebe a senha a ser cadastrada
     * @param strategyNames Recebe o nome da estrategia a ser colocado no portfolio de investimentos.
     * @param balance Recebe o saldo o qual sera adicionado ao portfolio.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a edicao de um cliente.
     */
    @PostMapping("/add")
    public ResponseEntity<String> addClient(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam String password, @RequestParam StrategyNames strategyNames, @RequestParam double balance){
        try{
            clientService.addClient(userId, portfolioId, password, strategyNames.getDisplayName(), balance);
            return ResponseEntity.ok("Cliente cadastrado com sucesso");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /** Metodo responsavel pela remocao de um usuario do sistema Swagger.
     * @param userId Recebe o ID do usuário associado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a remocao do cliente.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteClient(@RequestParam String userId) {
        try {
            clientService.deleteClientByClientID(userId);
            return ResponseEntity.ok("Cliente removido com sucesso!");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel pela edicao de um usuario cadastrado no sistema Swagger.
     * @param userId Recebe o ID do usuário associado.
     * @param password Recebe a nova senha a ser cadastrada.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a edicao do cliente.
     */
    @PostMapping("/edit")
    public ResponseEntity<String> updateClient(@RequestParam String userId, @RequestParam String password){
        try {
            clientService.updateClient(userId, password);
            return ResponseEntity.ok("Cliente atualizado com sucesso!");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}