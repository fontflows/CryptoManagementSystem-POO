package com.cryptomanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Classe que utiliza a Main, responsavel por executar todo o programa */
@SpringBootApplication
public class CryptoManagementApplication {

    /** Metodo Main que inicia a execução da aplicação.
     * @param args Recebe argumentos da linha de comando.
     */
    public static void main(String[] args) {
        SpringApplication.run(CryptoManagementApplication.class, args);
    }
}
