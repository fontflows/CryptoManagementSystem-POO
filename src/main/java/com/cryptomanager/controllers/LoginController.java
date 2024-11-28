package com.cryptomanager.controllers;

import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.services.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.cryptomanager.models.Client;

import java.io.IOException;


@Controller
public class LoginController {
    private final ClientService clientService;
    public ClientRepository clientRepository;
    public LoginController(ClientRepository clientRepository, ClientService clientService) {
        this.clientRepository = clientRepository;
        this.clientService = clientService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "userlogin";  // Nome do arquivo HTML (userlogin.html)
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) throws IOException {

        // Verificar se o usuário existe
        Client client = clientRepository.loadClientByID(username);
        if (client == null) {
            model.addAttribute("error", "Usuário não encontrado!");
            return "userlogin";
        }

        // Verificar se a senha está correta
        if (password.equalsIgnoreCase(client.getPassword())) {
            model.addAttribute("error", "Senha incorreta!");
            return "userlogin";
        }
            // Após autenticação bem-sucedida, redireciona para a página de sucesso
            return "redirect:/swagger-ui/index.html";  // Substitua por sua página inicial após o login
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String portfolioID,
                               @RequestParam String password,
                               @RequestParam String strategyName) {
        clientService.addClient(username, portfolioID, password, strategyName, 12);

        return "redirect:/login"; // Redireciona para a página de login após o cadastro
    }


}

