package com.cryptomanager.controllers;

import com.cryptomanager.exceptions.ClientServiceException;
import com.cryptomanager.exceptions.CryptoServiceException;
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


    @GetMapping("/Clients/get-all-Clients")
    public ResponseEntity<?> getAllClients() {
        try {
            return ResponseEntity.ok(clientService.getAllClientsToString());
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/Clients/search-by-id")
    public ResponseEntity<?> getClientByID(String userID) {
        try {
            return ResponseEntity.ok(clientService.getClientByClientIDToString(userID));
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/Clients/add")
    public ResponseEntity<String> addClient(@RequestParam String userID, @RequestParam String portfolioID, @RequestParam String password, @RequestParam StrategyNames strategyNames, @Parameter(description = "Role", schema = @Schema(allowableValues = {"CLIENT", "ADMIN"})) @RequestParam String role){
        try{
            clientService.addClient(userID, portfolioID, password, strategyNames.getDisplayName(), 0, role);
            return ResponseEntity.ok("Cliente cadastrado com sucesso");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/Clients/delete")
    public ResponseEntity<String> deleteClient(@RequestParam String userID) {
        try {
            clientService.deleteClientByClientID(userID);
            return ResponseEntity.ok("Cliente removido com sucesso!");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

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
    public ResponseEntity<String> updateUserRole(@RequestParam String userID, @Parameter(description = "Role", schema = @Schema(allowableValues = {"CLIENT", "ADMIN"})) @RequestParam String role){
        try {
            clientService.updateUserRole(userID, role);
            return ResponseEntity.ok("Role atualizada com sucesso!");
        } catch (ClientServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/Cryptos/add")
    public ResponseEntity<String> addCrypto(@RequestParam String cryptoName, @RequestParam double price, @RequestParam double growthRate, @RequestParam int riskFactor, @RequestParam double availableAmount) {
        try {
            cryptoService.addCrypto(cryptoName, price, growthRate, riskFactor, availableAmount);
            return ResponseEntity.ok("Criptomoeda adicionada com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/Cryptos/delete")
    public ResponseEntity<String> deleteCrypto(@RequestParam String cryptoName) {
        try {
            cryptoService.deleteCryptoByName(cryptoName);
            return ResponseEntity.ok("Criptomoeda removida com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

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

    @GetMapping("/Transactions/get-full-history")
    public ResponseEntity<String> getTransactionsHistory(@Parameter(description = "Transaction type", schema = @Schema(allowableValues = {"BUY", "SELL", "CONVERSION", "ALL"})) @RequestParam String transactionType) {
        try {
            return ResponseEntity.ok(transactionsService.getTransactionHistory(transactionType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/Transactions/get-history-by-ID")
    public ResponseEntity<String> getTransactionsHistoryByID(@RequestParam String userID, @Parameter(description = "Transaction type", schema = @Schema(allowableValues = {"BUY", "SELL", "CONVERSION", "ALL"})) @RequestParam String transactionType) {
        try {
            return ResponseEntity.ok(transactionsService.getTransactionHistoryByID(transactionType, userID));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
