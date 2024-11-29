package com.cryptomanager.config;

import com.cryptomanager.models.Client;
import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.repositories.LoginRepository;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class OpenApiConfig {

    private final ClientRepository clientRepository;
    private final LoginRepository loginRepository;

    public OpenApiConfig(ClientRepository clientRepository, LoginRepository loginRepository) {
        this.clientRepository = clientRepository;
        this.loginRepository = loginRepository;
    }

    @Bean
    public OpenApiCustomizer customOpenAPI() {
        return openApi -> {
            // Remove endpoints para usuários não autorizados
            openApi.getPaths().entrySet().removeIf(entry -> {
                String path = entry.getKey();
                try {
                    return isRestrictedEndpoint(path) && !hasPermissionForPath();
                } catch (IOException e) {
                    throw new RuntimeException("Erro ao carregar dados do usuario");
                }
            });
        };
    }

    private boolean isRestrictedEndpoint(String path) {
        String[] restricted = {"/cryptos/add", "/cryptos/edit", "/cryptos/delete", "/client/get-all-Clients", "/client/add", "/client/delete", "/report/create-crypto-or-client-report", "/transactions-history/get-history-by-ID", "/transactions-history/get-full-history", "/client/search-by-id", "/client/edit-passwords"};
        for(String check: restricted) {
            if(path.startsWith(check)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasPermissionForPath() throws IOException {;
        String clientID = loginRepository.loadLoggedInfo()[0];
        Client client = clientRepository.loadClientByID(clientID);
        return client.getRole().equalsIgnoreCase("ADMIN");
    }
}