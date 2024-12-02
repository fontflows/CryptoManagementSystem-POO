package com.cryptomanager.controllers;

import com.cryptomanager.services.ReportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Classe responsavel por realizar o relatorio dos portfolios gerados durante o uso do sistema Swagger.
 */
@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /** Metodo responsavel por criar o relatorio do portfolio especificado.
     * @param portfolioId Recebe o Id do portfolio.
     * @param userId Recebe o Id do usuario associado.
     * @return  Mensagem de retorno da correta execucao das funcoes associadas a criacao do relatorio desejado.
     */
    @PostMapping("/create-portifolio-report")
    public ResponseEntity<String> CreatePortifolioReport(@RequestParam String portfolioId, @RequestParam String userId) {
        try {
            int id = reportService.CreatePortifolioReport(userId, portfolioId);
            return ResponseEntity.ok(reportService.AcessReport(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/create-projected-portifolio-report")
    public ResponseEntity<String> CreateProjectedPortifolioReport(@RequestParam String portfolioid, @RequestParam String userid, @RequestParam int months) {
        try {
            int id = reportService.CreateProjectedPortifolioReport(userid, portfolioid, months);
            return ResponseEntity.ok(reportService.AcessReport(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/create-crypto-or-client-report")
    public ResponseEntity<String> CreateCryptoOrClientReport(@Parameter(description = "Report type", schema = @Schema(allowableValues = {"crypto", "client", "all"})) @RequestParam String reportType) {
        try {
            List <String> list = reportService.CreateListForReport(reportType);
            int id = reportService.CreateListReport(list);
            return ResponseEntity.ok(reportService.AcessReport(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/get-sum-reports")
    public ResponseEntity<String> GetSumReports() {
        try {
            return ResponseEntity.ok(reportService.GetSumReports());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/acess-report")
    public ResponseEntity<String> AcessReport(@RequestParam int reportid) {
        try {
            return ResponseEntity.ok(reportService.AcessReport(reportid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
