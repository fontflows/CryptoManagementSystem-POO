package com.cryptomanager.controllers;

import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import com.cryptomanager.services.ClientService;
import com.cryptomanager.services.CryptoService;
import com.cryptomanager.services.ReportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController{
    private final ReportService reportService;
    private final PortfolioRepository portfolioRepository;
    private final ClientService clientService;
    private final CryptoService cryptoService;

    @Autowired
    public ReportController(ReportService reportService, PortfolioRepository portfolioRepository,  ClientService clientService, CryptoService cryptoService) {
        this.reportService = reportService;
        this.portfolioRepository = portfolioRepository;
        this.clientService = clientService;
        this.cryptoService = cryptoService;
    }

    @PostMapping("/create-portifolio-report")
    public ResponseEntity<String> CreatePortifolioReport(@RequestParam String portfolioID,@RequestParam String userID) {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID);
        reportService.CreatePortifolioReport(portfolio);
        return ResponseEntity.ok("Relatório criado com sucesso!");
    }
    @PostMapping("/create-projected-portifolio-report")
    public ResponseEntity<String> CreateProjectedPortifolioReport(@RequestParam String portfolioid, @RequestParam String userid, @RequestParam int months) {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userid,portfolioid);
        reportService.CreateProjectedPortifolioReport(portfolio,months);
        return ResponseEntity.ok("Relatório criado com sucesso!");
    }
    @PostMapping("/create-crypto-or-client-report")
    public ResponseEntity<String> CreateCryptoOrClientReport(@Parameter(description = "Report type", schema = @Schema(allowableValues = {"crypto", "client","ambos"})) @RequestParam String reportType){

        List<String> list = (reportType.equals("client")) ? clientService.getAllClientsToString() : cryptoService.getAllCryptosToString();
        if (reportType.equals("ambos")) {
            list.addAll(clientService.getAllClientsToString());
        }
        reportService.CreateListReport(list);
        return ResponseEntity.ok("Relatório criado com sucesso!");
    }
}
