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

/**
 * Classe responsavel por lidar com a pertinencia de dados dos relatorios do sistema.
 */
@Repository
public class ReportRepository {
    private int id = readID();

    /** Metodo responsavel por ler o ID do relatorio informado.
     * @return Retorna o ID apos sua verificacao.
     */
    private int readID() {
        try (BufferedReader reader = new BufferedReader(new FileReader("reportConfig.txt"))) {
            String line = reader.readLine();
            return (line != null) ? Integer.parseInt(line): 0;
        } catch (IOException e) {
            return 0; // Em caso de erro, retorna 0
        }
    }

    /** Metodo responsavel por salvar o ID do relatorio informado.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a execucao do metodo.
     */
    private void saveID() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reportConfig.txt"))) {
            writer.write(Integer.toString(id));
        }
    }

    /** Metodo responsavel por gerar o relatorio do portfolio corrente no sistema.
     * @param portfolio Instancia que recebe o portfolio informado no sistema.
     * @return Retorna um indice associado ao relatorio gerado.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a execucao do metodo.
     */
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

    /** Metodo responsavel por gerar a projecao do relatorio, considerando o total de meses informado no sistema.
     * @param portfolio Instancia que recebe o portfolio associado.
     * @param months Recebe o total de meses informado no sistema.
     * @return Retorna um indice associado ao relatorio gerado.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a execucao do metodo.
     * @throws IllegalStateException Excecao lancada, caso ocorra a invocacao do metodo em um momento inadequado/ilegal.
     */
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

    /** Metodo responsavel por gerar um relatorio, a partir de uma lista formatada.
     * @param list Instancia que recebe a lista de Strings das informacoes, para compor o relatorio padronizado no arquivo "reportConfig.txt".
     * @return Retorna o indice associado a lista gerada.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a execucao do metodo.
     */
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

    /** Metodo responsavel por salvar o relatorio produzido no sistema.
     * @param report Insancia que recebe o relatorio gerado durante o uso do sistema, pelo usuario.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a execucao do metodo.
     */
    public void saveReport(String report) throws IOException{

        String PATH =  Integer.toString(id);
        id++;
        final String FILE_PATH = "report"+PATH+".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(report);
        }

        saveID();
    }

    /** Metodo responsavel por obter o sumario dos relatorios.
     * @return Retorna o sumario gerado/produzido dos relatorios, de maneira formatada.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a execucao do metodo.
     * @throws IllegalStateException Excecao lancada, caso ocorra a invocacao do metodo em um momento inadequado/ilegal.
     */
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

    /** Metodo responsavel por formatar o relatorio acessado especificamente.
     * @param idreport Recebe o id do relatorio de interesse.
     * @return Retorna o relatorio formatado devidamente.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a execucao do metodo.
     * @throws IllegalStateException Excecao lancada, caso ocorra a invocacao do metodo em um momento inadequado/ilegal.
     */
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