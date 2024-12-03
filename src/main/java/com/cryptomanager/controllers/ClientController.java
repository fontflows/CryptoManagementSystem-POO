package com.cryptomanager.controllers;

import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.repositories.LoginRepository;
import com.cryptomanager.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

/**Classe responsavel por facilitar a checagem e manipulacao de dados dos usuarios no sistema.*/
@RestController
@RequestMapping("1/client-info")
public class ClientController{
    private final ClientService clientService;
    private final LoginRepository loginRepository;

    /** Construtor ClientController
     * @param clientService Instancia utilizada para manipulacao dos dados dos clientes.
     * @param loginRepository Instancia que conecta o Controller com a classe que manipula os dados dos usuarios logados no arquivo.
     */
    @Autowired
    public ClientController(ClientService clientService, LoginRepository loginRepository) {
        this.clientService = clientService;
        this.loginRepository = loginRepository;
    }

    /** Metodo responsavel por obter as informacoes do usuario logado.
     * @return Retorna as informacoes do usuario logado.
     */
    @GetMapping("/get-own-info")
    public ResponseEntity<?> getLoggedClient() {
        try {
            return ResponseEntity.ok(clientService.getClientByClientIDToString(loginRepository.loadLoggedInfo()[0]));
        } catch (ClientServiceException | IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo reponsavel por permitir um usuario alterar sua propria senha.
     * @param newPassword Recebe a nova senha do usuario.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a edicao da senha do usuario.
     */
    @PostMapping("/edit-own-password")
    public ResponseEntity<String> updateLoggedClientPassword(@RequestParam String newPassword){
        try {
            clientService.updateClientPassword(loginRepository.loadLoggedInfo()[0], newPassword);
            return ResponseEntity.ok("Senha atualizada com sucesso!");
        } catch (ClientServiceException | IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}