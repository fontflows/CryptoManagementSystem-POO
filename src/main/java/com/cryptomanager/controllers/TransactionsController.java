package com.cryptomanager.controllers;

import com.cryptomanager.repositories.LoginRepository;
import com.cryptomanager.services.TransactionsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("5/transactions-history")
public class TransactionsController {
    private final TransactionsService transactionsService;
    private final LoginRepository loginRepository;

    @Autowired
    public TransactionsController(TransactionsService transactionsService, LoginRepository loginRepository) {
        this.transactionsService = transactionsService;
        this.loginRepository = loginRepository;
    }

    @GetMapping("/get-own-history")
    public ResponseEntity<String> getLoggedClientHistory(@Parameter(description = "Transaction type", schema = @Schema(allowableValues = {"BUY", "SELL", "CONVERSION", "ALL"})) @RequestParam String transactionType) {
        try {
            return ResponseEntity.ok(transactionsService.getTransactionHistoryByID(transactionType, loginRepository.loadLoggedInfo()[0]));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
