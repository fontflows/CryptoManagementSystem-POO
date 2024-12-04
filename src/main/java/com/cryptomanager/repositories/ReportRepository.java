package com.cryptomanager.repositories;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;
import com.cryptomanager.models.Portfolio;

import java.io.*;

import com.cryptomanager.services.InvestmentProjectionService;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.cryptomanager.repositories.TransactionsRepository.allListsToStringByID;


@Repository
public class ReportRepository {
    private int id = readID();

    private int readID() {
        try (BufferedReader reader = new BufferedReader(new FileReader("reportConfig.txt"))) {
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
    public int generateCurrentPortfolioReport(Portfolio portfolio) throws IOException {

        LocalDateTime reportDate = LocalDateTime.now();
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        double investedTotal = 0.0;
        double currentTotalValue = 0.0;
        String transactionHistory = allListsToStringByID(portfolio.getUserId());

        report.append("Data-Hora,PortId,Userid\n");
        // Cabeçalho do relatório: Data,id,userid;
        report.append(reportDate.format(formatter)).append(",")
                .append(portfolio.getId()).append(",")
                .append(portfolio.getUserId()).append("\n");

        if(!(portfolio.getInvestments().isEmpty())) {
            report.append("\nCryptoname,InvestedQuantity,purshacePrice,CryptoPrice,InvestedValue,CurrentValue,PercentageReturn\n");

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

            report.append("\nInvestedTotal,CurrentTotalValue,TotalPercentageReturn");
            // rodapé do portfolio : investedTotal,currentTotalValue,totalPercentageReturn
            double totalPercentageReturn = ((currentTotalValue - investedTotal) / investedTotal) * 100;
            report.append("\n")
                    .append(investedTotal).append(",")
                    .append(currentTotalValue).append(",")
                    .append(totalPercentageReturn).append("\n");
        }

        report.append("\nTransactions");
        report.append(transactionHistory);

        try {
            saveReport(report.toString());
            return id-1;
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
    public int generateProjectionReport(Portfolio portfolio,int months) throws IOException {
        if(portfolio.getInvestments().isEmpty())
            throw new IllegalStateException("Portfolio nao tem investimentos, logo nao pode criar report");

        LocalDateTime reportDate = LocalDateTime.now();
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        report.append("Data-Hora,PortId,Userid,Months\n");
        // RELATÓRIO DE PROJEÇÃO = reportdate,portid,Userid,meses
        report.append(reportDate.format(formatter)).append(",")
                .append(portfolio.getId()).append(",")
                .append(portfolio.getUserId()).append(",")
                .append(months).append("\n");

        if(!(portfolio.getInvestments().isEmpty())) {
            double totalProjectedValue = 0.0;
            double totalCurrentValue = 0.0;
            report.append("\nCryptoname,CurrentValueCrypto,Months,ProjectedValue,GrowthRate\n");

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
            report.append("\ntotalCurrentValue,TotalProjectedValue,ProjectedGrowth\n");
            // Sumário da projeção = totalCurrentValue,totalProjectedValue,projectedGrowth

            double projectedGrowth = ((totalProjectedValue - totalCurrentValue) / totalCurrentValue) * 100;

            report.append(totalCurrentValue).append(",")
                    .append(totalProjectedValue).append(",")
                    .append(projectedGrowth).append(",\n");
        }

        try {
            saveReport(report.toString());
            return id-1;
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
    public int generateListReport(List<String> list) throws IOException{
        LocalDateTime reportDate = LocalDateTime.now();
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        report.append("Data-Hora\n");
        report.append(reportDate.format(formatter)).append("\n");
        for (String element : list)
            report.append(element).append("\n");

        try {
            saveReport(report.toString());
            return id-1;
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
    public StringBuilder getSumReports() throws IOException{
        if(readID() <= 0)
            throw new IllegalStateException("Não há relatórios");

        StringBuilder out = new StringBuilder();

        for(int i = 0;i<id;i++){
            try(BufferedReader reader = new BufferedReader(new FileReader("report"+i+".txt"))) {
                out.append("report ").append(i).append(" :");
                reader.readLine(); //descarta o título
                out.append(reader.readLine()).append("\n");
            }
        }
        return out;
    }
    public StringBuilder acessReport(int idreport) throws IOException{
        if(readID() <= 0)
            throw new IllegalStateException("Não há relatórios");

        StringBuilder out = new StringBuilder();
        String Path = "report" + idreport + ".txt";
        try(BufferedReader reader = new BufferedReader(new FileReader(Path))) {
            String line;
            while ((line = reader.readLine()) != null)
                out.append(line).append("\n");
        }
        return out;
    }
}