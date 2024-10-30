package com.cryptomanager.services;

import com.cryptomanager.exceptions.InvestmentReportExceptions;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.InvestmentReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            throw new InvestmentReportExceptions("Erro ao criar relatorio: " + portfolio.getId(), e);
        }
    }
    public void CreateProjectedPortifolioRepository(Portfolio portfolio, int meses){
        try {
            investmentReportRepository.generateProjectionReport(portfolio,meses);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio projecao: {}", portfolio.getId() + " " + meses, e);
            throw new InvestmentReportExceptions("Erro ao criar relatorio de projecao: " + portfolio.getId() + " " + meses, e);
        }
    }


}