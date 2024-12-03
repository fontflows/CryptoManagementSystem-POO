package com.cryptomanager.services;

import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.models.Client;
import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.repositories.LoginRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;

/** Classe responsavel por manipular os dados dos usuarios que realizam o login, implementa a interface UserDetailsService do Spring Security */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final ClientRepository clientRepository;
    private final LoginRepository loginRepository;

    /** Construtor CustomUserDetailsService
     * @param clientRepository Instancia que conecta o Service com a classe que manipula os dados dos clientes no arquivo.
     * @param loginRepository Instancia que conecta o Service com a classe que manipula os dados dos usuarios logados no arquivo.
     */
    public CustomUserDetailsService(ClientRepository clientRepository, LoginRepository loginRepository) {
        this.clientRepository = clientRepository;
        this.loginRepository = loginRepository;
    }

    /** Metodo padrao da interface sobreposto para carregar o usuario pelo seu identificador obtido no login.
     * @param username Recebe o identificador do usuario que realiza o login.
     * @return Retorna uma instancia de UserDetails que é utilizado pelo Spring Security para verificar os dados de login.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        Client client;
        try {
            client = clientRepository.loadClientByID(username);
            loginRepository.saveLoggedInfo(username, client.getPortfolio().getId());
        } catch (IOException e) {
            throw new ClientServiceException("Erro ao verificar cadastro de cliente", e);
        } catch (NoSuchElementException e) {
            throw new ClientServiceException("Cliente não encontrado", e);
        }
        return User.builder()
                .username(client.getClientID())
                .password("{noop}" + client.getPassword())
                .roles(client.getRole())
                .build();
    }
}
