package com.cryptomanager.config;

import com.cryptomanager.models.Client;
import com.cryptomanager.repositories.ClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final ClientRepository clientRepository;

    public SecurityConfig(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cryptos/add", "/cryptos/edit", "/cryptos/delete").hasRole("ADMIN")
                        .requestMatchers("/client/get-all-Clients", "/client/add", "/client/delete").hasRole("ADMIN")
                        .requestMatchers("/report/create-crypto-or-client-report").hasRole("ADMIN")
                        .requestMatchers("/cryptos", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {})
                .formLogin(formLogin -> formLogin
                        .defaultSuccessUrl("/swagger-ui/index.html", true)
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() throws IOException {
        List<Client> clients = clientRepository.loadClients();
        User.UserBuilder users = User.builder();
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        for(Client client : clients){
            manager.createUser(users.username(client.getClientID()).password("{noop}" + client.getPassword()).roles(client.getRole()).build());
        }
        return manager;
    }
}