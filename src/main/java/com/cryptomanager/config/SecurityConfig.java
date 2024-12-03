package com.cryptomanager.config;

import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.repositories.LoginRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
                        .requestMatchers("/Admin/**").hasRole("ADMIN")
                        .requestMatchers("/1/**").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers("/2/**").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers("/3/**").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers("/4/**").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers("/5/**").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {})
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")  // Caminho para a sua página de login
                        .defaultSuccessUrl("/swagger-ui/index.html", true)  // URL de redirecionamento após o login bem-sucedido
                        .failureUrl("/login?error=login")
                        .permitAll()  // Permitir acesso à página de login sem autenticação
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(clientRepository, loginRepository);
    }
}
