package com.cryptomanager.controllers;

import com.cryptomanager.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**Classe responsavel por manipular os metodos de autenticacao do sistema*/
@Controller
public class AuthController {
    private final ClientService clientService;

    /** Construtor AuthController
     * @param clientService Instancia utilizada para manipulacao dos dados dos clientes.
     */
    @Autowired
    public AuthController(ClientService clientService) {
        this.clientService = clientService;
    }

    /** Metodo responsavel por referenciar a pagina de autenticacao personalizada ao sistema.
     * @return Retorna o nome do arquivo da pagina de autenticacao personalizada
     */
    @GetMapping("/login")
    public String showAuthenticationForm() {
        return "userLogin";  // Nome do arquivo HTML (userLogin.html)
    }

    /** Metodo responsavel por receber os dados de cadastro realizados atraves da pagina de autenticacao personalizada.
     * @param username Recebe o nome de usuario utilizado como userID.
     * @param portfolioID Recebe o ID do portfolio do usuario.
     * @param password Recebe a senha do usuario.
     * @param strategyName Recebe a estrategia de investimento do portfolio do usuario.
     * @return Retorna a pagina que o usuario sera redirecionado depois de se cadastrar.
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String portfolioID,
                               @RequestParam String password,
                               @RequestParam String strategyName) {
        try {
            clientService.addClient(username, portfolioID, password, strategyName, 0, "UNAUTHORIZED");
        } catch (Exception e) {
            return "redirect:/login?error=register"; // Redireciona para a página de login com um alerta após o cadastro falhar.
        }

        return "redirect:/login?successRegister"; // Redireciona para a página de login após o cadastro com sucesso
    }


}

