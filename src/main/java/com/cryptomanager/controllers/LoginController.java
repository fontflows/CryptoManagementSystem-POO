package com.cryptomanager.controllers;

import com.cryptomanager.models.Client;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class LoginController {
    private final PortfolioRepository portfolioRepository;
    public ClientRepository clientRepository;
    public LoginController(ClientRepository clientRepository, PortfolioRepository portfolioRepository) {
        this.clientRepository = clientRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "userlogin";  // Nome do arquivo HTML (userlogin.html)
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String portfolioID,
                               @RequestParam String password,
                               @RequestParam String role) throws IOException {
        // Criação do novo cliente
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(username, portfolioID);
        Client newClient = new Client(username, portfolio, password, role);
        clientRepository.saveClient(newClient);

        return "redirect:/login"; // Redireciona para a página de login após o cadastro
    }


}

