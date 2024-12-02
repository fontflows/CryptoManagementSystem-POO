package com.cryptomanager.controllers;

import com.cryptomanager.repositories.LoginRepository;
import com.cryptomanager.services.ReportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Classe responsavel por gerar relatorios de portfolios, criptomoedas e clientes no Swagger.
 */
@RestController
@RequestMapping("4/report")
public class ReportController {
    private final ReportService reportService;
    private final LoginRepository loginRepository;

    @Autowired
    public ReportController(ReportService reportService, LoginRepository loginRepository) {
        this.reportService = reportService;
        this.loginRepository = loginRepository;
    }

    /** Metodo responsavel por criar o relatorio do portfolio do usuario logado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a criacao do relatorio desejado.
     */
    @PostMapping("/create-portifolio-report")
    public ResponseEntity<String> CreatePortifolioReport() {
        try {
            int id = reportService.CreatePortifolioReport(loginRepository.loadLoggedInfo()[0], loginRepository.loadLoggedInfo()[1]);
            return ResponseEntity.ok(reportService.AcessReport(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/create-projected-portifolio-report")
    public ResponseEntity<String> CreateProjectedPortifolioReport(@RequestParam int months) {
        try {
            int id = reportService.CreateProjectedPortifolioReport(loginRepository.loadLoggedInfo()[0], loginRepository.loadLoggedInfo()[1], months);
            return ResponseEntity.ok(reportService.AcessReport(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
