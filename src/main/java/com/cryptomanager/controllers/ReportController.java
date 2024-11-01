package com.cryptomanager.controllers;

import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.PortfolioRepository;
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
    private final PortfolioRepository portfolioRepository;
    @Autowired
    public ReportController(InvestmentReportService investmentReportService, PortfolioRepository portfolioRepository) {
        this.investmentReportService = investmentReportService;
        this.portfolioRepository = portfolioRepository;
    }

    @PostMapping("/create-portifolio-report")
    public ResponseEntity<String> CreatePortifolioRepository(@RequestBody String portfolioid,@RequestBody String userid) {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(portfolioid,userid);
        investmentReportService.CreatePortifolioRepository(portfolio);
        return ResponseEntity.ok("Portfólio adicionado ou atualizado com sucesso!");
    }
    @PostMapping("/create-projected-portifolio-report")
    public ResponseEntity<String> CreateProjectedPortifolioRepository(@RequestBody String portfolioid,@RequestBody String userid,@RequestBody int meses) {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(portfolioid,userid);
        investmentReportService.CreateProjectedPortifolioRepository(portfolio,meses);
        return ResponseEntity.ok("Portfólio adicionado ou atualizado com sucesso!");

    }
}
