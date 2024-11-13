package com.cryptomanager.controllers;

import com.cryptomanager.services.InvestmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController{
    private final InvestmentReportService investmentReportService;

    @Autowired
    public ReportController(InvestmentReportService investmentReportService) {
        this.investmentReportService = investmentReportService;
    }

    @PostMapping("/create-portifolio-report")
    public ResponseEntity<String> CreatePortifolioRepository(@RequestParam String portfolioID,@RequestParam String userID) {
        investmentReportService.CreatePortifolioReport(userID, portfolioID);
        return ResponseEntity.ok("Relatório criado com sucesso!");
    }
    @PostMapping("/create-projected-portifolio-report")
    public ResponseEntity<String> CreateProjectedPortifolioRepository(@RequestParam String portfolioID, @RequestParam String userID, @RequestParam int months) {
        investmentReportService.CreateProjectedPortifolioReport(portfolioID, userID, months);
        return ResponseEntity.ok("Relatório criado com sucesso!");

    }
}
