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

        double investedTotal = 0.0;
        double currentTotalValue = 0.0;



        // Cabeçalho do relatório
        //RELATÓRIO DE PORTFOLIO: Data,id,userid;
        report.append(reportDate.format(formatter)).append(",")
                .append(portfolio.getId()).append(",")
                .append(portfolio.getUserId()).append("\n");

        // Detalhes dos investimentos


        for (Investment investment : portfolio.getInvestments()) {
            CryptoCurrency crypto = investment.getCryptoCurrency();
            double investedQuantity = investment.getCryptoInvestedQuantity();
            double purchasePrice = investment.getPurchasePrice();
            double investedValue = investedQuantity * purchasePrice;
            double currentValue = investedQuantity * crypto.getPrice();
            double percentageReturn = ((currentValue - investedValue) / investedValue) * 100;

            // Conteúdo: cryptoname,qntinvestida,cryptoPrice,investedValue,currentValue,percentageReturn

            report.append(crypto.getName()).append(",")
                    .append(investedQuantity).append(",")
                    .append(purchasePrice).append(",")
                    .append(crypto.getPrice()).append(",")
                    .append(investedValue).append(",")
                    .append(currentValue).append(",")
                    .append(percentageReturn).append("\n");

            investedTotal += investedValue;
            currentTotalValue += currentValue;
        }

        // Sumário do portfolio : investedTotal,currentTotalValue,totalPercentageReturn

        double totalPercentageReturn = ((currentTotalValue - investedTotal) / investedTotal) * 100;
        report.append("\n")
                .append(investedTotal).append(",")
                .append(currentTotalValue).append(",")
                .append(totalPercentageReturn).append("\n");

        return report.toString();
    }
    public String generateProjectionReport(Portfolio portfolio,int meses) {
        LocalDateTime reportDate = LocalDateTime.now();
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // RELATÓRIO DE PROJEÇÃO = reportdate,meses
        report.append(reportDate.format(formatter)).append(",")
                .append(meses).append("\n");

        double totalProjectedValue = 0.0;
        double totalCurrentValue = 0.0;
        for (Investment investment : portfolio.getInvestments()) {
            CryptoCurrency crypto = investment.getCryptoCurrency();
            double investedQuantity = investment.getCryptoInvestedQuantity();
            double currentValue = investedQuantity * crypto.getPrice();

            // Utiliza o InvestmentProjectionService para calcular a projeção
            double projectedvalue = InvestmentProjectionService.calculateInvestmentProjection(investedQuantity, crypto, meses);
            // Conteúdo = cryptoname, valoratualcrypto, meses , valorprojetado, crescimento mensal%,
            report.append(crypto.getName()).append(",")
                    .append(currentValue).append(",")
                    .append(meses).append(",")
                    .append(projectedvalue).append(",")
                    .append(crypto.getGrowthRate() * 100).append(",\n");

            totalProjectedValue += projectedvalue;
            totalCurrentValue += currentValue;
        }

        // Sumário da projeção = totalCurrentValue,totalProjectedValue,projectedGrowth

        double projectedGrowth = ((totalProjectedValue - totalCurrentValue) / totalCurrentValue) * 100;

        report.append(totalCurrentValue).append(",")
                .append(totalProjectedValue).append(",")
                .append(projectedGrowth).append(",\n");

        return report.toString();
    }


}