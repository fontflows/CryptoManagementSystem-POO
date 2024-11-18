package com.cryptomanager.controllers;

import com.cryptomanager.services.ClientService;
import com.cryptomanager.services.CryptoService;
import com.cryptomanager.services.ReportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController{
    private final ReportService reportService;
    private final ClientService clientService;
    private final CryptoService cryptoService;

    @Autowired
    public ReportController(ReportService reportService, ClientService clientService, CryptoService cryptoService) {
        this.reportService = reportService;
        this.clientService = clientService;
        this.cryptoService = cryptoService;
    }

    @PostMapping("/create-portifolio-report")
    public ResponseEntity<String> CreatePortifolioReport(@RequestParam String portfolioID,@RequestParam String userID) {
        reportService.CreatePortifolioReport(userID, portfolioID);
        return ResponseEntity.ok("Relatório criado com sucesso!");
    }
    @PostMapping("/create-projected-portifolio-report")
    public ResponseEntity<String> CreateProjectedPortifolioReport(@RequestParam String portfolioid, @RequestParam String userid, @RequestParam int months) {
        reportService.CreateProjectedPortifolioReport(userid, portfolioid, months);
        return ResponseEntity.ok("Relatório criado com sucesso!");
    }
    @PostMapping("/create-crypto-or-client-report")
    public ResponseEntity<String> CreateCryptoOrClientReport(@Parameter(description = "Report type", schema = @Schema(allowableValues = {"crypto", "client","all"})) @RequestParam String reportType){

        List<String> list = (reportType.equals("client")) ? clientService.getAllClientsToString() : cryptoService.getAllCryptosToString();
        if (reportType.equals("all")) {
            list.addAll(clientService.getAllClientsToString());
        }
        reportService.CreateListReport(list);
        return ResponseEntity.ok("Relatório criado com sucesso!");
    }

    @PostMapping("/get-sum-reports")
    public ResponseEntity<String> GetSumReports(){
        try {
            return ResponseEntity.ok(reportService.GetSumReports());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
