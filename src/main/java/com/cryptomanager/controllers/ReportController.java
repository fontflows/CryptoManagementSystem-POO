package com.cryptomanager.controllers;

import com.cryptomanager.models.Portfolio;
import com.cryptomanager.services.InvestmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController{
    private final InvestmentReportService investmentReportService;

    @Autowired
    public ReportController(InvestmentReportService investmentReportService) {
        this.investmentReportService = investmentReportService;
    }

    @PostMapping("/create-portifolio-report")
    public ResponseEntity<String> CreatePortifolioRepository(@RequestBody Portfolio portfolio) {
        investmentReportService.CreatePortifolioRepository(portfolio);
        return ResponseEntity.ok("Portfólio adicionado ou atualizado com sucesso!");
    }
    @PostMapping("/create-projected-portifolio-report")
    public ResponseEntity<String> CreateProjectedPortifolioRepository(@RequestBody Portfolio portfolio,@RequestBody int meses) {
        investmentReportService.CreateProjectedPortifolioRepository(portfolio,meses);
        return ResponseEntity.ok("Portfólio adicionado ou atualizado com sucesso!");

    }
}
