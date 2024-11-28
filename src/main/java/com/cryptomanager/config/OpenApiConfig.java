package com.cryptomanager.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer customOpenAPI() {
        return openApi -> {
            // Remove endpoints para usuários não autorizados
            openApi.getPaths().entrySet().removeIf(entry -> {
                String path = entry.getKey(); // O endpoint
                return isRestrictedEndpoint(path) && !hasPermissionForPath();
            });
        };
    }

    private boolean isRestrictedEndpoint(String path) {
        String[] restricted = {"/cryptos/add", "/cryptos/edit", "/cryptos/delete", "/client/get-all-Clients", "/client/add", "/client/delete", "/report/create-crypto-or-client-report"};
        for(String check: restricted) {
            if(path.startsWith(check)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasPermissionForPath() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}