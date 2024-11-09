package com.cryptomanager.services;

import com.cryptomanager.exceptions.ReportExceptions;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public void CreatePortifolioReport(Portfolio portfolio){
        try {
            reportRepository.generateCurrentPortfolioReport(portfolio);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio: {}", portfolio.getId(), e);
            throw new ReportExceptions("Erro ao criar relatorio: " + portfolio.getId(), e);
        }
    }
    public void CreateProjectedPortifolioReport(Portfolio portfolio, int months){
        try {
            reportRepository.generateProjectionReport(portfolio,months);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio projecao: {}", portfolio.getId() + " " + months, e);
            throw new ReportExceptions("Erro ao criar relatorio de projecao: " + portfolio.getId() + " " + months, e);
        }
    }
    public void CreateListReport(List <String> list){
        try{
            reportRepository.generateListReport(list);
        } catch (IOException e) {
            throw new ReportExceptions("Erro ao criar relatorio",e);
        }
    }

}
