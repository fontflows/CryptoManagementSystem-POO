package com.cryptomanager.controllers;

import com.cryptomanager.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AuthController {
    private final ClientService clientService;

    @Autowired
    public AuthController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "userlogin";  // Nome do arquivo HTML (userlogin.html)
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String portfolioID,
                               @RequestParam String password,
                               @RequestParam String strategyName) {
        try {
            clientService.addClient(username, portfolioID, password, strategyName, 0, "CLIENT");
        } catch (Exception e) {
            return "redirect:/login?error=register";
        }

        return "redirect:/login"; // Redireciona para a página de login após o cadastro
    }


}

