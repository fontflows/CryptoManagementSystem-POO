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
