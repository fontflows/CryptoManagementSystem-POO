package com.cryptomanager.controllers;

import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.exceptions.CryptoServiceException;
import com.cryptomanager.exceptions.PortfolioHasInvestmentsException;
import com.cryptomanager.models.StrategyNames;
import com.cryptomanager.services.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Admin")
public class AdminController {
    private final ClientService clientService;
    private final CryptoService cryptoService;
    private final ReportService reportService;
    private final TransactionsService transactionsService;

    public AdminController(ClientService clientService, CryptoService cryptoService, ReportService reportService, TransactionsService transactionsService) {
        this.clientService = clientService;
        this.cryptoService = cryptoService;
        this.reportService = reportService;
        this.transactionsService = transactionsService;
    }

    /** Metodo responsavel por obter todos os clientes que estao cadastrados no sistema.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a obtencao de todos os clientes cadastrados no sistema.
     */
    @GetMapping("/Clients/get-all-Clients")
    public ResponseEntity<?> getAllClients() {
        try {
            return ResponseEntity.ok(clientService.getAllClientsToString());
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel por pesquisar o usuario de interesse, conforme o seu id informado.
     * @param userID Recebe o Id do usuario associado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a busca de um cliente, conforme o seu Id.
     */
    @GetMapping("/Clients/search-by-id")
    public ResponseEntity<?> getClientByID(String userID) {
        try {
            return ResponseEntity.ok(clientService.getClientByClientIDToString(userID));
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel por adicionar clientes no sistema Swagger.
     * @param userID Recebe o ID do usuario associado.
     * @param portfolioID Recebe o ID do portfolio do usuario associado.
     * @param password Recebe a senha a ser cadastrada
     * @param strategyNames Recebe o nome da estrategia a ser colocado no portfolio de investimentos.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a edicao de um cliente.
     */
    @PostMapping("/Clients/add")
    public ResponseEntity<String> addClient(@RequestParam String userID, @RequestParam String portfolioID, @RequestParam String password, @RequestParam StrategyNames strategyNames, @Parameter(description = "Role", schema = @Schema(allowableValues = {"CLIENT", "ADMIN", "UNAUTHORIZED"})) @RequestParam String role){
        try{
            clientService.addClient(userID, portfolioID, password, strategyNames.getDisplayName(), 0, role);
            return ResponseEntity.ok("Cliente cadastrado com sucesso");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /** Metodo responsavel pela remocao de um usuario do sistema Swagger.
     * @param userID Recebe o ID do usuário associado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a remocao do cliente.
     */
    @DeleteMapping("/Clients/delete")
    public ResponseEntity<String> deleteClient(@RequestParam String userID) {
        try {
            clientService.deleteClientByClientID(userID);
            return ResponseEntity.ok("Cliente removido com sucesso!");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel pela edicao da senha de um usuario cadastrado no sistema Swagger.
     * @param userID Recebe o ID do usuário associado.
     * @param password Recebe a nova senha a ser cadastrada.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a edicao da senha do cliente.
     */
    @PostMapping("/Clients/edit-password-by-ID")
    public ResponseEntity<String> updateClient(@RequestParam String userID, @RequestParam String password){
        try {
            clientService.updateClientPassword(userID, password);
            return ResponseEntity.ok("Senha atualizada com sucesso!");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/Clients/edit-role-by-ID")
    public ResponseEntity<String> updateUserRole(@RequestParam String userID, @Parameter(description = "Role", schema = @Schema(allowableValues = {"CLIENT", "ADMIN", "UNAUTHORIZED"})) @RequestParam String role){
        try {
            clientService.updateUserRole(userID, role);
            return ResponseEntity.ok("Role atualizada com sucesso!");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel por adicionar dada criptomoeda de interesse ao sistema Swagger.
     * @param cryptoName Recebe o nome.
     * @param price Recebe o preco.
     * @param growthRate Recebe a taxa de crescimento.
     * @param riskFactor Recebe o fator de risco.
     * @param availableAmount Recebe a quantidade da criptomoeda disponivel no mercado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a adicao da criptomoeda.
     */
    @PostMapping("/Cryptos/add")
    public ResponseEntity<String> addCrypto(@RequestParam String cryptoName, @RequestParam double price, @RequestParam double growthRate, @RequestParam int riskFactor, @RequestParam double availableAmount) {
        try {
            cryptoService.addCrypto(cryptoName, price, growthRate, riskFactor, availableAmount);
            return ResponseEntity.ok("Criptomoeda adicionada com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /** Metodo responsavel pela remocao da criptomoeda de interesse do sistema Swagger.
     * @param cryptoName Recebe o nome.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a remocao da criptomoeda.
     */
    @DeleteMapping("/Cryptos/delete")
    public ResponseEntity<String> deleteCrypto(@RequestParam String cryptoName) {
        try {
            cryptoService.deleteCryptoByName(cryptoName);
            return ResponseEntity.ok("Criptomoeda removida com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel pela edicao de certa criptomoeda presente no sistema Swagger.
     * @param cryptoName Recebe o nome.
     * @param fieldToEdit Recebe o campo o qual se deseja editar na chamada do metodo.
     * @param newValue Recebe o novo valor a ser atrelado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a edicao da criptomoeda.
     */
    @PostMapping("/Cryptos/edit")
    public ResponseEntity<String> updateCrypto(@RequestParam String cryptoName, @Parameter(description = "Edit field", schema = @Schema(allowableValues = {"Price", "Growth Rate", "Risk Factor"})) @RequestParam String fieldToEdit, @RequestParam String newValue) {
        try{
            cryptoService.updateCrypto(cryptoName, fieldToEdit, newValue);
            return ResponseEntity.ok("Criptomoeda atualizada com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/Report/create-crypto-or-client-report")
    public ResponseEntity<String> CreateCryptoOrClientReport(@Parameter(description = "Report type", schema = @Schema(allowableValues = {"crypto", "client", "all"})) @RequestParam String reportType) {
        try {
            List<String> list = reportService.CreateListForReport(reportType);
            int id = reportService.CreateListReport(list);
            return ResponseEntity.ok(reportService.AcessReport(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/Reports/get-reports-summary")
    public ResponseEntity<String> GetSumReports() {
        try {
            return ResponseEntity.ok(reportService.GetSumReports());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/Reports/acess-report-by-ID")
    public ResponseEntity<String> AcessReport(@RequestParam int reportID) {
        try {
            return ResponseEntity.ok(reportService.AcessReport(reportID));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel por obter/informar todo o historico de transacoes que ocorreram no sistema Swagger
     * @param transactionType Recebe o tipo de transacao realizada (Compra, Venda, Conversao ou Todas, respectivamente)
     * @return Mensagem de retorno da correta execucao das funcoes associadas a obtencao do historico de todas as transacoes realizadas no sistema.
     */
    @GetMapping("/Transactions/get-full-history")
    public ResponseEntity<String> getTransactionsHistory(@Parameter(description = "Transaction type", schema = @Schema(allowableValues = {"BUY", "SELL", "CONVERSION", "ALL"})) @RequestParam String transactionType) {
        try {
            return ResponseEntity.ok(transactionsService.getTransactionHistory(transactionType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel por obter/informar todo o historico de transacoes de um usuario, a partir do seu Id.
     * @param userID Recebe o Id do usuario.
     * @param transactionType Recebe o tipo de transacao o qual ocorreu, considerando o Id do usuario especificado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a obtencao do historico de transacoes do usuario, conforme o seu Id.
     */
    @GetMapping("/Transactions/get-history-by-ID")
    public ResponseEntity<String> getTransactionsHistoryByID(@RequestParam String userID, @Parameter(description = "Transaction type", schema = @Schema(allowableValues = {"BUY", "SELL", "CONVERSION", "ALL"})) @RequestParam String transactionType) {
        try {
            return ResponseEntity.ok(transactionsService.getTransactionHistoryByID(transactionType, userID));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
