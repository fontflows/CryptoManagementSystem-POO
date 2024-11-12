package com.cryptomanager.repositories;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.services.InvestmentProjectionService;

import java.io.*;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class ReportRepository {
    private int id = readID();

    public ReportRepository() throws IOException {
    }

    private int readID() {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("reportConfig.txt")))) {
            String line = reader.readLine();
            return (line != null) ? Integer.parseInt(line): 0;
        } catch (IOException e) {
            return 0; // Em caso de erro, retorna 0
        }
    }

    private void saveID() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reportConfig.txt"))) {
            writer.write(Integer.toString(id));
        }
    }
    public void generateCurrentPortfolioReport(Portfolio portfolio) throws IOException {

        LocalDateTime reportDate = LocalDateTime.now();
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        double investedTotal = 0.0;
        double currentTotalValue = 0.0;



        // Cabeçalho do relatório: Data,id,userid;
        report.append(reportDate.format(formatter)).append(",")
                .append(portfolio.getId()).append(",")
                .append(portfolio.getUserId()).append("\n");

        for (Investment investment : portfolio.getInvestments()) {
            CryptoCurrency crypto = investment.getCryptoCurrency();
            double investedQuantity = investment.getCryptoInvestedQuantity();
            double purchasePrice = investment.getPurchasePrice();
            double investedValue = investedQuantity * purchasePrice;
            double currentValue = investedQuantity * crypto.getPrice();
            double percentageReturn = ((currentValue - investedValue) / investedValue) * 100;

            // Conteúdo: cryptoname,investedQuantity,purshacePrice,cryptoPrice,investedValue,currentValue,percentageReturn

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

        // rodapé do portfolio : investedTotal,currentTotalValue,totalPercentageReturn

        double totalPercentageReturn = ((currentTotalValue - investedTotal) / investedTotal) * 100;
        report.append("\n")
                .append(investedTotal).append(",")
                .append(currentTotalValue).append(",")
                .append(totalPercentageReturn).append("\n");

        try {
            saveReport(report.toString());
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
    public void generateProjectionReport(Portfolio portfolio,int months) throws IOException {
        LocalDateTime reportDate = LocalDateTime.now();
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // RELATÓRIO DE PROJEÇÃO = reportdate,meses
        report.append(reportDate.format(formatter)).append(",")
                .append(months).append("\n");

        double totalProjectedValue = 0.0;
        double totalCurrentValue = 0.0;
        for (Investment investment : portfolio.getInvestments()) {
            CryptoCurrency crypto = investment.getCryptoCurrency();
            double investedQuantity = investment.getCryptoInvestedQuantity();
            double currentValue = investedQuantity * crypto.getPrice();

            // Utiliza o InvestmentProjectionService para calcular a projeção
            double projectedvalue = InvestmentProjectionService.calculateInvestmentProjection(investedQuantity, crypto, months);
            // Conteúdo = cryptoname, valoratualcrypto, meses , valorprojetado, crescimento mensal%,
            report.append(crypto.getName()).append(",")
                    .append(currentValue).append(",")
                    .append(months).append(",")
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

        try {
            saveReport(report.toString());
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
    public void generateListReport(List<String> list) throws IOException{
        LocalDateTime reportDate = LocalDateTime.now();
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (String element : list){
            report.append(element).append("\n");
        }
        try {
            saveReport(report.toString());
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
    public void saveReport(String report) throws IOException{

        String PATH =  Integer.toString(id);
        id++;
        final String FILE_PATH = "report"+PATH+".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(report);
        }
        saveID();
    }

}