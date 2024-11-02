package com.cryptomanager.controllers;

import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.PortfolioRepository;
import com.cryptomanager.services.InvestmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController{
    private final InvestmentReportService investmentReportService;
    private final PortfolioRepository portfolioRepository;
    @Autowired
    public ReportController(InvestmentReportService investmentReportService, PortfolioRepository portfolioRepository) {
        this.investmentReportService = investmentReportService;
        this.portfolioRepository = portfolioRepository;
    }

    @PostMapping("/create-portifolio-report")
    public ResponseEntity<String> CreatePortifolioRepository(@RequestParam String portfolioid,@RequestParam String userid) {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userid,portfolioid);
        investmentReportService.CreatePortifolioReport(portfolio);
        return ResponseEntity.ok("Relatório criado com sucesso!");
    }
    @PostMapping("/create-projected-portifolio-report")
    public ResponseEntity<String> CreateProjectedPortifolioRepository(@RequestParam String portfolioid, @RequestParam String userid, @RequestParam int months) {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userid,portfolioid);
        investmentReportService.CreateProjectedPortifolioReport(portfolio,months);
        return ResponseEntity.ok("Relatório criado com sucesso!");

    }
}
