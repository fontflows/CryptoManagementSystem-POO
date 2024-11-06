package com.cryptomanager.repositories;

import com.cryptomanager.models.*;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.cryptomanager.services.PortfolioService.findInvestment;
import static com.cryptomanager.services.PortfolioService.hasAsset;

@Repository
public class PortfolioRepository {
    private static final String FILE_PATH = "portfolio.txt";

    // Adiciona um portfólio novo
    public void addPortfolio(Portfolio portfolio) throws IOException {
        if (!isValidPortfolio(portfolio)) {
            throw new IllegalArgumentException("Portfólio não encontrado");
        }
        savePortfolio(portfolio);
    }

    //Adiciona um portfolio novo no arquivo
    private void savePortfolio(Portfolio portfolio) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(portfolio.toString());
        }
    }

    // Carrega o portfólio especificado por ID e userId
    public Portfolio loadPortfolioByUserIdAndPortfolioId(String userId, String portfolioId) {
        List<Portfolio> allPortfolios = loadAllPortfolios();
        return findPortfolio(allPortfolios, portfolioId, userId);
    }

    // Remover ativo de um portfólio específico
    public void removeAssetFromPortfolio(String portfolioId, String userId, String assetName) {
        try {
            Portfolio portfolio = loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);
            if (hasAsset(assetName, portfolio)) {
                Investment removedInvestment = findInvestment(portfolio, assetName);
                portfolio.getInvestments().remove(removedInvestment);
                updatePortfolio(portfolio);  // Salva as mudanças
            }

            else
                throw new IllegalArgumentException("Criptomoeda não encontrada no portfólio");
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Erro ao remover ativo: " + e.getMessage());
        }
    }

    // Atualiza todos os portfólios no arquivo
    public void updatePortfolio(Portfolio updatedPortfolio) {
        List<Portfolio> allPortfolios = loadAllPortfolios();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portfolio portfolio : allPortfolios) {
                if(updatedPortfolio.getId().equals(portfolio.getId())){
                    writer.write(updatedPortfolio.toString());
                }

                else
                    writer.write(portfolio.toString());
            }
        } catch (IOException e) {
            System.err.println("Erro ao atualizar portfólios: " + e.getMessage());
        }
    }

    // Carrega todos os portfólios do arquivo
    public List<Portfolio> loadAllPortfolios() {
        List<Portfolio> portfolioList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            Portfolio currentPortfolio = null;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length == 4) {
                    currentPortfolio = new Portfolio(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
                    portfolioList.add(currentPortfolio);
                }

                else if (parts.length >= 8 && currentPortfolio != null) {
                    Investment investment = createInvestmentFromParts(parts);
                    currentPortfolio.getInvestments().add(investment);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar portfólios: " + e.getMessage());
        }
        return portfolioList;
    }

    // Função auxiliar para criar um investimento a partir de uma linha de texto
    private Investment createInvestmentFromParts(String[] parts) {
        if (parts.length < 8)
            throw new IllegalArgumentException("Dados do investimento mal formados");

        String cryptoName = parts[0];
        double price = Double.parseDouble(parts[1]);
        double growthRate = Double.parseDouble(parts[2]);
        double marketCap = Double.parseDouble(parts[3]);
        double volume24h = Double.parseDouble(parts[4]);
        int riskFactor = Integer.parseInt(parts[5]);
        double quantity = Double.parseDouble(parts[6]);
        double purchasePrice = Double.parseDouble(parts[7]);

        CryptoCurrency cryptoCurrency = new CryptoCurrency(cryptoName, price, growthRate, marketCap, volume24h, riskFactor);
        return new Investment(cryptoCurrency, purchasePrice, quantity);
    }

    // Encontrar portfólio por ID e userId
    private Portfolio findPortfolio(List<Portfolio> portfolios, String portfolioId, String userId) {
        for(Portfolio portfolio: portfolios){
            if(portfolio.getUserId().equals(userId) && portfolio.getId().equals(portfolioId)){
                return portfolio;
            }
        }
        throw new IllegalArgumentException("Portfolio não encontrado");
    }

    // Validação de portfólio
    public boolean isValidPortfolio(Portfolio portfolio) {
        if (portfolio == null) return false;

        if (portfolio.getUserId() == null || portfolio.getUserId().isEmpty()) {
            System.err.println("Erro: userId não pode ser nulo ou vazio.");
            return false;
        }

        if (portfolio.getId() == null || portfolio.getId().isEmpty()) {
            System.err.println("Erro: portfolioId não pode ser nulo ou vazio.");
            return false;
        }

        if (portfolio.getInvestments() == null || portfolio.getInvestments().isEmpty()) {
            System.err.println("Erro: Portfólio deve conter pelo menos um ativo.");
            return false;
        }

        return true;
    }

    public void appendConversionToFile(Portfolio portfolio, String fromCryptoName, String toCryptoName, double balance, double convertedQuantity) {
        List<Portfolio> allPortfolios = loadAllPortfolios();
        Portfolio targetPortfolio = null;

        for (Portfolio p : allPortfolios) {
            if (p.getId().equals(portfolio.getId())) {
                targetPortfolio = p;
                break;
            }
        }

        if (targetPortfolio == null) {
            System.err.println("Erro: Portfólio não encontrado.");
            return;
        }

        // Realiza a conversão no arquivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("portfolios.txt"))) {
            // Reescreve todos os portfólios no arquivo
            for (Portfolio p : allPortfolios) {
                writer.write(p.getId() + "," + p.getUserId() + "\n");

                // Verifica se está sendo escrito o portfólio alvo para aplicar a conversão
                if (p.getId().equals(targetPortfolio.getId())) {
                    for (Investment investment : p.getInvestments()) {
                        String cryptoName = investment.getCryptoCurrency().getName();
                        double cryptoQuantity = investment.getCryptoInvestedQuantity();

                        if (cryptoName.equals(fromCryptoName))
                            // Ajusta a quantidade da criptomoeda de origem
                            cryptoQuantity -= balance;

                        else if (cryptoName.equals(toCryptoName))
                            // Ajusta a quantidade da criptomoeda de destino
                            cryptoQuantity += convertedQuantity;

                        // Escreve o investimento atualizado no arquivo
                        writer.write("Criptomoeda: " + cryptoName + "," + "\n" +
                                "Preço: " + investment.getCryptoCurrency().getPrice() + "," + "\n" +
                                "Taxa de crescimento: " + investment.getCryptoCurrency().getGrowthRate() + "," + "\n" +
                                "Cotação de mercado: " + investment.getCryptoCurrency().getMarketCap() + "," + "\n" +
                                "Volume transitado em um dia:" + investment.getCryptoCurrency().getVolume24h() + "," + "\n" +
                                "Saldo convertido: " + cryptoQuantity + "," + "\n" +
                                "Preço de compra associado: " + investment.getPurchasePrice() + "\n");
                    }
                }

                else {
                    // Escreve os outros portfólios sem alterações
                    for (Investment investment : p.getInvestments()) {
                        writer.write("Criptomoeda: " + investment.getCryptoCurrency().getName() + "," + "\n" +
                                "Preço: " + investment.getCryptoCurrency().getPrice() + "," + "\n" +
                                "Taxa de crescimento: " + investment.getCryptoCurrency().getGrowthRate() + "," + "\n" +
                                "Cotação de mercado: " + investment.getCryptoCurrency().getMarketCap() + "," + "\n" +
                                "Volume transitado em um dia:" + investment.getCryptoCurrency().getVolume24h() + "," + "\n" +
                                "Quantidade investida: " + investment.getCryptoInvestedQuantity() + "," + "\n" +
                                "Preço de compra: " + investment.getPurchasePrice() + "\n");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao atualizar o arquivo de portfólios: " + e.getMessage());
        }
    }
}
