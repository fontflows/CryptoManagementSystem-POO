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

/** Classe responsavel pela configuracao da seguranca da aplicacao utilizando Spring Security*/
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final ClientRepository clientRepository;
    private final LoginRepository loginRepository;

    /** Construtor SecurityConfig
     * @param clientRepository Instancia que permite a conexao com a classe que manipula os dados dos Portfolios no arquivo.
     * @param loginRepository Instancia que permite a conexao com a classe que manipula os dados dos usuarios logados no arquivo.
     */
    public SecurityConfig(ClientRepository clientRepository, LoginRepository loginRepository) {
        this.clientRepository = clientRepository;
        this.loginRepository = loginRepository;
    }

    /** Metodo responsavel por configurar as regras de seguranca dos endpoints da API, definindo como usuarios podem acessar diferentes partes da aplicacao.
     * @param http instancia da classe HttpSecurity, que faz parte da configuracao de segurança no Spring Security
     * @return Retorna uma instancia de SecurityFilterChain com as configuracoes de seguranca definidas
     * @throws Exception Se ocorrer um erro durante a configuracao de seguranca.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login").permitAll() // Permite acesso livre nas paginas de login e cadastro
                        .requestMatchers("/Admin/**").hasRole("ADMIN") // Restringe o acesso aos endpoints definidos em /Admin/
                        .requestMatchers("/1/**").hasAnyRole("CLIENT", "ADMIN") // Permite acesso apenas para usuarios autorizados nos demais endpoints
                        .requestMatchers("/2/**").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers("/3/**").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers("/4/**").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers("/5/**").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {})
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")  // Caminho para a pagina de autenticação personalizada
                        .defaultSuccessUrl("/swagger-ui/index.html", true)  // URL de redirecionamento após o login bem-sucedido
                        .failureUrl("/login?error=login") // URL de redirecionamento após o login mal-sucedido
                        .permitAll()  // Permite acesso à página de login sem autenticação
                );

        return http.build();
    }

    /** Metodo responsavel por carregar informacoes de login dos usuarios para efetuar autenticacao.
     * @return Retorna uma instancia de UserDetailsService com as informacoes de usuario armazenadas no sistema para comparar com as inseridas na pagina de login.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(clientRepository, loginRepository);
    }
}
