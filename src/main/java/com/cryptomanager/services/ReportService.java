package com.cryptomanager.services;

import com.cryptomanager.exceptions.ReportExceptions;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.PortfolioRepository;
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
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository, PortfolioRepository portfolioRepository) {
        this.reportRepository = reportRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public void CreatePortifolioReport(String userID, String portfolioID){
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID);
            reportRepository.generateCurrentPortfolioReport(portfolio);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio: {}", portfolioID, e);
            throw new ReportExceptions("Erro ao criar relatorio: " + portfolioID, e);
        }
    }
    public void CreateProjectedPortifolioReport(String userID, String portfolioID, int months){
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID);
            reportRepository.generateProjectionReport(portfolio,months);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio projecao: {}", portfolioID + " " + months, e);
            throw new ReportExceptions("Erro ao criar relatorio de projecao: " + portfolioID + " " + months, e);
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
