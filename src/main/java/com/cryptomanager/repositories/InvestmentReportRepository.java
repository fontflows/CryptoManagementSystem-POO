package com.cryptomanager.repositories;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.services.InvestmentProjectionService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InvestmentReportRepository{

    public String generateCurrentPortfolioReport (Portfolio portfolio) {
        LocalDateTime reportDate = LocalDateTime.now();
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        double totalInvestido = 0.0;
        double totalValorAtual = 0.0;



        // Cabeçalho do relatório
        //RELATÓRIO DE PORTFOLIO: Data,id,userid;
        report.append(reportDate.format(formatter)).append(",")
                .append(portfolio.getId()).append(",")
                .append(portfolio.getUserId()).append("\n");

        // Detalhes dos investimentos


        for (Investment investment : portfolio.getInvestments()) {
            CryptoCurrency crypto = investment.getCryptoCurrency();
            double quantidadeInvestida = investment.getCryptoInvestedQuantity();
            double precoCompra = investment.getPurchasePrice();
            double valorInvestido = quantidadeInvestida * precoCompra;
            double valorAtual = quantidadeInvestida * crypto.getPrice();
            double percentualRetorno = ((valorAtual - valorInvestido) / valorInvestido) * 100;

            // Conteúdo: cryptoname,qntinvestida,cryptoPrice,valorInvestido,valorAtual,percentualRetorno

            report.append(crypto.getName()).append(",")
                    .append(quantidadeInvestida).append(",")
                    .append(precoCompra).append(",")
                    .append(crypto.getPrice()).append(",")
                    .append(valorInvestido).append(",")
                    .append(valorAtual).append(",")
                    .append(percentualRetorno).append("\n");

            totalInvestido += valorInvestido;
            totalValorAtual += valorAtual;
        }

        // Sumário do portfolio : totalInvestido,totalValorAtual,retornoTotalPercentual

        double retornoTotalPercentual = ((totalValorAtual - totalInvestido) / totalInvestido) * 100;
        report.append("\n")
                .append(totalInvestido).append(",")
                .append(totalValorAtual).append(",")
                .append(retornoTotalPercentual).append("\n");

        return report.toString();
    }
    public String generateProjectionReport(Portfolio portfolio,int meses) {
        LocalDateTime reportDate = LocalDateTime.now();
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // RELATÓRIO DE PROJEÇÃO = reportdate,meses
        report.append(reportDate.format(formatter)).append(",")
                .append(meses).append("\n");

        double valorTotalProjetado = 0.0;
        double valorTotalAtual = 0.0;
        for (Investment investment : portfolio.getInvestments()) {
            CryptoCurrency crypto = investment.getCryptoCurrency();
            double quantidadeInvestida = investment.getCryptoInvestedQuantity();
            double valorAtual = quantidadeInvestida * crypto.getPrice();

            // Utiliza o InvestmentProjectionService para calcular a projeção
            double valorProjetado = InvestmentProjectionService.calculateInvestmentProjection(quantidadeInvestida, crypto, meses);
            // Conteúdo = cryptoname, valoratualcrypto, meses , valorprojetado, crescimento mensal%,
            report.append(crypto.getName()).append(",")
                    .append(valorAtual).append(",")
                    .append(meses).append(",")
                    .append(valorProjetado).append(",")
                    .append(crypto.getGrowthRate() * 100).append(",\n");

            valorTotalProjetado += valorProjetado;
            valorTotalAtual += valorAtual;
        }

        // Sumário da projeção = valorTotalAtual,valorTotalProjetado,crescimentoProjetado

        double crescimentoProjetado = ((valorTotalProjetado - valorTotalAtual) / valorTotalAtual) * 100;

        report.append(valorTotalAtual).append(",")
                .append(valorTotalProjetado).append(",")
                .append(crescimentoProjetado).append(",\n");

        return report.toString();
    }


}