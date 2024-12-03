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

/** Classe responsavel pelos metodos Service para geracao de relatorios*/
@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final ReportRepository reportRepository;
    private final PortfolioRepository portfolioRepository;
    private final CryptoRepository cryptoRepository;
    private final ClientRepository clientRepository;

    /** Construtor ReportService
     * @param reportRepository Instancia que conecta o Service a classe que manipula os relatorios no arquivo.
     * @param portfolioRepository Instancia que conecta o Service a classe que manipula os dados dos portfolios no arquivo.
     * @param cryptoRepository Instancia que conecta o Service a classe que manipula os dados das criptomoedas no arquivo.
     * @param clientRepository Instancia que conecta o Service a classe que manipula os dados dos clientes no arquivo.
     */
    @Autowired
    public ReportService(ReportRepository reportRepository, PortfolioRepository portfolioRepository, CryptoRepository cryptoRepository, ClientRepository clientRepository) {
        this.reportRepository = reportRepository;
        this.portfolioRepository = portfolioRepository;
        this.cryptoRepository = cryptoRepository;
        this.clientRepository = clientRepository;
    }

    /** Metodo responsavel por gerar um relatorio contendo os dados atuais de um portfolio.
     * @param userID Recebe o userID do usuario associado.
     * @param portfolioID Recebe o portfolioID do portfolio associado.
     * @return Retorna o indice do relatorio gerado, alem de gerar um .txt com as informacoes do relatorio.
     * @throws ReportExceptions Caso ocorra algum erro ao obter o relatorio.
     */
    public int CreatePortifolioReport(String userID, String portfolioID){
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID);
            return reportRepository.generateCurrentPortfolioReport(portfolio);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio: {}", portfolioID, e);
            throw new ReportExceptions("Erro ao criar relatorio: " + portfolioID, e);
        }
    }

    /** Metodo responsavel por gerar um relatorio contendo projecoes de investimento de um portfolio.
     * @param userID Recebe o userID do usuario associado.
     * @param portfolioID Recebe o portfolioID do portfolio associado.
     * @param months Recebe a quantidade de meses referente ao tempo de investimento considerado na projecao.
     * @return Retorna o indice do relatorio gerado, alem de gerar um .txt com as informacoes do relatorio.
     * @throws ReportExceptions Caso ocorra algum erro ao obter o relatorio.
     */
    public int CreateProjectedPortifolioReport(String userID, String portfolioID, int months){
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID,portfolioID);
            return reportRepository.generateProjectionReport(portfolio,months);
        } catch (IOException e) {
            logger.error("Erro ao criar relatorio projecao: {}", portfolioID + " " + months, e);
            throw new ReportExceptions("Erro ao criar relatorio de projecao: " + portfolioID + " " + months, e);
        }
    }

    /** Metodo responsavel por gerar um relatorio formatado a partir de uma lista de dados.
     * @param list Recebe a lista que sera formatada.
     * @return Retorna o indice do relatorio gerado, alem de gerar um .txt com as informacoes do relatorio.
     * @throws ReportExceptions Caso ocorra algum erro ao obter o relatorio.
     */
    public int CreateListReport(List <String> list){
        try{
            return reportRepository.generateListReport(list);
        } catch (IOException e) {
            throw new ReportExceptions("Erro ao criar relatorio",e);
        }
    }

    /** Metodo responsavel por obter o sumario de todos os relatorios gerados no sistema.
     * @return Retorna o sumario de todos os relatorios gerados no sistema.
     * @throws ReportExceptions Caso ocorra algum erro ao obter o sumario.
     */
    public String GetSumReports(){
        try{
            return reportRepository.getSumReports().toString();
        } catch (IOException e){
            throw new ReportExceptions("Erro ao acessar relatorios",e);
        } catch (IllegalStateException e) {
            throw new ReportExceptions("Não há relatórios",e);
        }
    }

    /** Metodo responsavel por exibir as informacoes de um relatorio baseado em seu ID.
     * @param reportid Recebe o ID do relatorio a ser exibido.
     * @return Retorna o relatorio solicitado baseado no ID.
     * @throws ReportExceptions Caso ocorra algum erro ao obter o relatorio.
     */
    public String AcessReport(int reportid){
        try{
            return reportRepository.acessReport(reportid).toString();
        } catch (IOException e) {
            throw new ReportExceptions("Erro ao acessar relatório",e);
        }
    }

    /** Metodo responsavel por gerar uma lista para ser utilizada nos relatorios.
     * @param reportType Recebe o tipo de relatorio que esta sendo gerado.
     * @return Retorna uma lista formatada para ser utilizada na geracao de um relatorio.
     * @throws ReportExceptions Caso ocorra algum erro ao criar a lista para o relatorio.
     */
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