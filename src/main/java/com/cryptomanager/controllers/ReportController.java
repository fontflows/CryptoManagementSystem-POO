package com.cryptomanager.controllers;

import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.PortfolioRepository;
import com.cryptomanager.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController{
    private final ReportService reportService;
    private final PortfolioRepository portfolioRepository;
    @Autowired
    public ReportController(ReportService reportService, PortfolioRepository portfolioRepository) {
        this.reportService = reportService;
        this.portfolioRepository = portfolioRepository;
    }

    @PostMapping("/create-portifolio-report")
    public ResponseEntity<String> CreatePortifolioRepository(@RequestParam String portfolioID,@RequestParam String userID) {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID);
        reportService.CreatePortifolioReport(portfolio);
        return ResponseEntity.ok("Relatório criado com sucesso!");
    }
    @PostMapping("/create-projected-portifolio-report")
    public ResponseEntity<String> CreateProjectedPortifolioRepository(@RequestParam String portfolioid, @RequestParam String userid, @RequestParam int months) {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userid,portfolioid);
        reportService.CreateProjectedPortifolioReport(portfolio,months);
        return ResponseEntity.ok("Relatório criado com sucesso!");

    }
}
