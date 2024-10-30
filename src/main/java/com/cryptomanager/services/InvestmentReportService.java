package com.cryptomanager.services;

import com.cryptomanager.exceptions.CryptoServiceException;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.InvestmentReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class InvestmentReportService{

    private static final Logger logger = LoggerFactory.getLogger(InvestmentReportService.class);
    private final InvestmentReportRepository investmentReportRepository;

    Portfolio portfolio;

    public InvestmentReportService(InvestmentReportRepository investmentReportRepository, Portfolio portfolio) {
        this.investmentReportRepository = investmentReportRepository;
        this.portfolio = portfolio;
    }

    public void CreatePortifolioRepository(Portfolio portfolio){
        try {
            investmentReportRepository.generateCurrentPortfolioReport(portfolio);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio: {}", portfolio.getId(), e);
            throw new CryptoServiceException("Erro ao criar relatorio: " + portfolio.getId(), e);
        }
    }


}