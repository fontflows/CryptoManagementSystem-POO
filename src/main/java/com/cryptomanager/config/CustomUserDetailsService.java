package com.cryptomanager.config;

import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.models.Client;
import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.repositories.LoginRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.NoSuchElementException;

public class CustomUserDetailsService implements UserDetailsService {
    private final ClientRepository clientRepository;
    private final LoginRepository loginRepository;

    public CustomUserDetailsService(ClientRepository clientRepository, LoginRepository loginRepository) {
        this.clientRepository = clientRepository;
        this.loginRepository = loginRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws NoSuchElementException {
        Client client;
        try {
            client = clientRepository.loadClientByID(username);
        } catch (IOException e) {
            throw new ClientServiceException("Erro ao verificar cadastro de cliente", e);
        }
        try {
            loginRepository.saveLoggedInfo(username, client.getPortfolio().getId());
        } catch (IOException e) {
            throw new ClientServiceException("Erro ao verificar cadastro de login", e);
        }
        return User.builder()
                .username(client.getClientID())
                .password("{noop}" + client.getPassword())
                .roles(client.getRole())
                .build();
    }
}
