package com.cryptomanager.services;

import com.cryptomanager.exceptions.InvestmentReportExceptions;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.InvestmentReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Service
public class InvestmentReportService{

    private static final Logger logger = LoggerFactory.getLogger(InvestmentReportService.class);
    private final InvestmentReportRepository investmentReportRepository;

    @Autowired
    public InvestmentReportService(InvestmentReportRepository investmentReportRepository) {
        this.investmentReportRepository = investmentReportRepository;
    }

    public void CreatePortifolioReport(Portfolio portfolio){
        try {
            investmentReportRepository.generateCurrentPortfolioReport(portfolio);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio: {}", portfolio.getId(), e);
            throw new InvestmentReportExceptions("Erro ao criar relatorio: " + portfolio.getId(), e);
        }
    }
    public void CreateProjectedPortifolioReport(Portfolio portfolio, int months){
        try {
            investmentReportRepository.generateProjectionReport(portfolio,months);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio projecao: {}", portfolio.getId() + " " + months, e);
            throw new InvestmentReportExceptions("Erro ao criar relatorio de projecao: " + portfolio.getId() + " " + months, e);
        }
    }


}
