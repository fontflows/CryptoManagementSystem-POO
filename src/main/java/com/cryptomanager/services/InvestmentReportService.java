package com.cryptomanager.services;

import com.cryptomanager.exceptions.InvestmentReportExceptions;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.InvestmentReportRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Service
public class InvestmentReportService{

    private static final Logger logger = LoggerFactory.getLogger(InvestmentReportService.class);
    private final InvestmentReportRepository investmentReportRepository;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public InvestmentReportService(InvestmentReportRepository investmentReportRepository, PortfolioRepository portfolioRepository) {
        this.investmentReportRepository = investmentReportRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public void CreatePortifolioReport(String userID, String portfolioID){
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID);
            investmentReportRepository.generateCurrentPortfolioReport(portfolio);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio: {}", portfolioID, e);
            throw new InvestmentReportExceptions("Erro ao criar relatorio: " + portfolioID, e);
        }
    }
    public void CreateProjectedPortifolioReport(String userID, String portfolioID, int months){
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID);
            investmentReportRepository.generateProjectionReport(portfolio,months);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio projecao: {}", portfolioID + " " + months, e);
            throw new InvestmentReportExceptions("Erro ao criar relatorio de projecao: " + portfolioID + " " + months, e);
        }
    }


}
