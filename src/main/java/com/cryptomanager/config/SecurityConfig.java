package com.cryptomanager.config;

import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.models.Client;
import com.cryptomanager.repositories.ClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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
                        .requestMatchers("/login", "/cryptos/add", "/cryptos/edit", "/cryptos/delete").hasRole("ADMIN")
                        .requestMatchers("/client/get-all-Clients", "/client/add", "/client/delete").hasRole("ADMIN")
                        .requestMatchers("/report/create-crypto-or-client-report").hasRole("ADMIN")
                        .requestMatchers("/cryptos", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {})
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")  // Caminho para a sua página de login
                        .defaultSuccessUrl("/swagger-ui/index.html", true)  // URL de redirecionamento após o login bem-sucedido
                        .permitAll()  // Permitir acesso à página de login sem autenticação
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(clientRepository);
    }
}
