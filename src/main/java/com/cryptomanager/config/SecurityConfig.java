package com.cryptomanager.config;

import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.repositories.LoginRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final ClientRepository clientRepository;
    private final LoginRepository loginRepository;

    public SecurityConfig(ClientRepository clientRepository, LoginRepository loginRepository) {
        this.clientRepository = clientRepository;
        this.loginRepository = loginRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login").permitAll()
                        .requestMatchers("/cryptos/add", "/cryptos/edit", "/cryptos/delete").hasRole("ADMIN")
                        .requestMatchers("/client/get-all-Clients", "/client/add", "/client/delete", "/client/search-by-id", "/client/edit-passwords").hasRole("ADMIN")
                        .requestMatchers("/report/create-crypto-or-client-report").hasRole("ADMIN")
                        .requestMatchers("/transactions-history/get-history-by-ID", "/transactions-history/get-full-history").hasRole("ADMIN")
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
        return new CustomUserDetailsService(clientRepository, loginRepository);
    }
}
