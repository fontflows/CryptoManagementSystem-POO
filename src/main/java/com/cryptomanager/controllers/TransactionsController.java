package com.cryptomanager.controllers;

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
@RequestMapping("/Transactions-History")
public class TransactionsController {
    private final TransactionsService transactionsService;

    @Autowired
    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @GetMapping("/get-full-history")
    public ResponseEntity<String> getTransactionsHistory(@Parameter(description = "Transaction type", schema = @Schema(allowableValues = {"BUY", "SELL", "CONVERSION", "ALL"})) @RequestParam String transactionType) {
        try {
            return ResponseEntity.ok(transactionsService.getTransactionHistory(transactionType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/get-history-by-ID")
    public ResponseEntity<String> getTransactionsHistoryByID(@RequestParam String userID, @Parameter(description = "Transaction type", schema = @Schema(allowableValues = {"BUY", "SELL", "CONVERSION", "ALL"})) @RequestParam String transactionType) {
        try {
            return ResponseEntity.ok(transactionsService.getTransactionHistoryByID(transactionType, userID));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
