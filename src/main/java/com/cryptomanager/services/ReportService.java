package com.cryptomanager.services;

import com.cryptomanager.exceptions.ReportExceptions;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.ClientRepository;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import com.cryptomanager.repositories.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.cryptomanager.repositories.TransactionsRepository.allListsToString;


@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final ReportRepository reportRepository;
    private final PortfolioRepository portfolioRepository;
    private final CryptoRepository cryptoRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository, PortfolioRepository portfolioRepository, CryptoRepository cryptoRepository, ClientRepository clientRepository) {
        this.reportRepository = reportRepository;
        this.portfolioRepository = portfolioRepository;
        this.cryptoRepository = cryptoRepository;
        this.clientRepository = clientRepository;
    }

    public int CreatePortifolioReport(String userID, String portfolioID){
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID);
            return reportRepository.generateCurrentPortfolioReport(portfolio);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio: {}", portfolioID, e);
            throw new ReportExceptions("Erro ao criar relatorio: " + portfolioID, e);
        }
    }
    public int CreateProjectedPortifolioReport(String userID, String portfolioID, int months){
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID);
            return reportRepository.generateProjectionReport(portfolio,months);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio projecao: {}", portfolioID + " " + months, e);
            throw new ReportExceptions("Erro ao criar relatorio de projecao: " + portfolioID + " " + months, e);
        }
    }
    public int CreateListReport(List <String> list){
        try{
            return reportRepository.generateListReport(list);
        } catch (IOException e) {
            throw new ReportExceptions("Erro ao criar relatorio",e);
        }
    }
    public String GetSumReports(){
        try{
            return reportRepository.getSumReports().toString();
        } catch (IOException e){
            throw new ReportExceptions("Erro ao acessar relatorios",e);
        } catch (IllegalStateException e) {
            throw new ReportExceptions("Não há relatórios",e);
        }
    }
    public String AcessReport(int reportid){
        try{
            return reportRepository.acessReport(reportid).toString();
        } catch (IOException e) {
            throw new ReportExceptions("Erro ao acessar relatório",e);
        }
    }
    public List <String> CreateListForReport(String reportType){
        try{
            List <String> list = new ArrayList<>();
            switch (reportType) {
                case "client" -> {
                    list.add("ClientId,PortfolioId,Password");
                    list.addAll(clientRepository.loadClientsToString());
                    list.add(allListsToString());
                }
                case "crypto" -> {
                    list.add("Name,Price,GrowthRate,MarketCap,Volume24h,RiskFactor,InvestorsAmount,TotalAmount,AvailableAmount");
                    list.addAll(cryptoRepository.loadCryptosToString());
                }
                case "all" -> {
                    list.add("ClientId,PortfolioId,Password");
                    list.addAll(clientRepository.loadClientsToString());
                    list.add(allListsToString());
                    list.add("\nName,Price,GrowthRate,MarketCap,Volume24h,RiskFactor,InvestorsAmount,TotalAmount,AvailableAmount");
                    list.addAll(cryptoRepository.loadCryptosToString());
                }
            }
            return list;
        } catch (IOException e) {
            throw new ReportExceptions("Erro ao criar lista",e);
        }
    }

}
