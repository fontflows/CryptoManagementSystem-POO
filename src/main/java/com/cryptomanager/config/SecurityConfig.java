package com.cryptomanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cryptos/add").hasRole("ADMIN")
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
    public UserDetailsService userDetailsService() {
        // Criando o usuário ADMIN com função ADMIN usando um encoder padrão
        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin123")  // {noop} indica que a senha não será codificada
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}
