package com.cryptomanager.services;

import com.cryptomanager.exceptions.PortfolioNotFoundException;
import com.cryptomanager.models.*;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;

import static com.cryptomanager.repositories.CryptoRepository.loadCryptoByName;
import static com.cryptomanager.repositories.TransactionsRepository.saveBuyTransaction;
import static com.cryptomanager.repositories.TransactionsRepository.saveSellTransaction;
import static com.cryptomanager.services.InvestmentStrategyService.getInvestmentStrategyByName;
import static com.cryptomanager.services.InvestmentStrategyService.getRandomCrypto;

/** Classe responsavel pelos metodos Service do Portfolio.*/
@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final CryptoRepository cryptoRepository;

    /** Constructor PortfolioService
     * @param portfolioRepository Instancia que conecta o Service com a classe que manipula os dados dos Portfolios no arquivo.
     * @param cryptoRepository Instancia que conecta o Service com a classe que manipula os dados das criptomoedas no arquivo.
     */
    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, CryptoRepository cryptoRepository) {
        this.portfolioRepository = portfolioRepository;
        this.cryptoRepository = cryptoRepository;
    }

    /** Calcula o valor total investido em um Portfolio.
     * @param userId Identificador do usuario cujo Portfolio sera utilizado.
     * @param portfolioId Identificador do Portfolio do usuario.
     * @return {@code double} Valor total em investimentos do Portfolio especificado.
     */
    public double calculateTotalValue(String userId, String portfolioId) {
        try {
            double totalValue = 0.0;
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);

            for (Investment investment : portfolio.getInvestments()) {
                CryptoCurrency cryptoCurrency = investment.getCryptoCurrency();
                double actualPrice = cryptoCurrency.getPrice();
                double quantity = investment.getCryptoInvestedQuantity();
                totalValue += actualPrice * quantity;
            }

            return totalValue;
        } catch (NoSuchElementException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado para o usuário " + userId + ": " + e.getMessage(), e);
        }
    }


    /** Encontra um Investment em um Portfolio baseado no nome da criptomoeda.
     * @param portfolio Portfolio que sera realizado a busca pelo Investment.
     * @param cryptoName Nome da criptomoeda armazenada no Investment procurado.
     * @return {@code Investment} Investimento buscado.
     * @throws NoSuchElementException Caso nao seja encontrado nenhum investimento com a criptomoeda especificada.
     */
    public static Investment findInvestment(Portfolio portfolio, String cryptoName) {
        for (Investment investment : portfolio.getInvestments()) {
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(cryptoName.trim()))
                return investment;
        }
        throw new NoSuchElementException("Investimento não encontrado para a criptomoeda " + cryptoName);
    }

    /** Verifica se um dado Portfolio possui uma criptomoeda como investimento.
     * @param cryptoName Nome da criptomoeda cuja existencia no Portfolio sera verificada.
     * @param portfolio Portfolio no qual sera realizado a busca pela criptomoeda.
     * @return {@code boolean} Se a criptomoeda se encontra no Portfolio ou nao.
     */
    public static boolean hasCrypto(String cryptoName, Portfolio portfolio) {
        for (Investment investment : portfolio.getInvestments())
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(cryptoName.trim()))
                return true;
        return false;
    }

    /** Sugere uma CryptoCurrency baseado na estrategia de investimento de um Portfolio especificado.
     * @param userID Identificador do usuario cujo Portfolio sera utilizado.
     * @param portfolioID Identificador do Portfolio do usuario.
     * @return {@code CryptoCurrency} Sugestao baseada na estrategia de investimento.
     * @throws IOException Caso ocorra um erro na leitura dos Portfolios no arquivo.
     * @throws IllegalArgumentException Caso alguma entrada seja invalida.
     * @throws NoSuchElementException Caso uma criptomoeda ou o Portfolio nao seja encontrado.
     */
    public CryptoCurrency suggestCryptoCurrency(String userID, String portfolioID) {
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
            InvestmentStrategy investmentStrategy = portfolio.getInvestmentStrategy();
            try {
                InvestmentStrategyService.updateInvestmentStrategyList(investmentStrategy);
                if (investmentStrategy.getSuggestedCryptos().isEmpty())
                    throw new NoSuchElementException("Nenhuma criptomoeda disponível para sugestão na estratégia " + investmentStrategy.getInvestmentStrategyName());
                return getRandomCrypto(investmentStrategy);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException(e.getMessage());
            }
        } catch (PortfolioNotFoundException e) {
            throw new PortfolioNotFoundException(e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro ao sugerir criptomoeda: " + e.getMessage(), e);
        }
    }

    /** Atualiza a estrategia de investimentos de um Portfolio.
     * @param userID Identificador do usuario cujo Portfolio sera utilizado.
     * @param portfolioID Identificador do Portfolio do usuario.
     * @param strategyName Nome da nova estrategia que sera utilizada no Portfolio.
     * @throws IOException Caso ocorra um erro na leitura dos Portfolios no arquivo.
     */
    public void setPortfolioInvestmentStrategy(String userID, String portfolioID, String strategyName) throws IOException {
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);

            if (strategyName.equals(portfolio.getInvestmentStrategy().getInvestmentStrategyName()))
                return; // Caso selecione a mesma estratégia, não é necessário alterar nada

            InvestmentStrategy strategy = getInvestmentStrategyByName(strategyName);

            portfolio.setInvestmentStrategy(strategy);
            portfolioRepository.updatePortfolio(portfolio);

        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro interno do servidor durante a aplicação do investimento: " + e.getMessage(), e);
        }
    }

    /** Adiciona saldo em um Portfolio.
     * @param userID Identificador do usuario cujo Portfolio sera utilizado.
     * @param portfolioID Identificador do Portfolio do usuario.
     * @param amount Quantidade de saldo que sera adicionado.
     * @throws IOException Caso ocorra um erro na leitura dos Portfolios no arquivo.
     */
    public void addBalance(String userID, String portfolioID, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Valor inserido para adicionar saldo deve ser maior do que zero");

        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
            portfolio.setBalance(portfolio.getBalance() + amount);
            portfolioRepository.updatePortfolio(portfolio);

        } catch (NoSuchElementException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro ao adicionar saldo no portfólio: " + e.getMessage(), e);
        }
    }

    /** Resgata saldo em um Portfolio.
     * @param userID Identificador do usuario cujo Portfolio sera utilizado.
     * @param portfolioID Identificador do Portfolio do usuario.
     * @param amount Quantidade de saldo que sera resgatado.
     * @throws IOException Caso ocorra um erro na leitura dos Portfolios no arquivo.
     */
    public void redeemBalance(String userID, String portfolioID, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Valor inserido para resgatar deve ser maior que zero");

        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);

            if (amount > portfolio.getBalance())
                throw new IllegalArgumentException("Valor inserido para resgatar é maior do que o saldo disponível");

            portfolio.setBalance(portfolio.getBalance() - amount);
            portfolioRepository.updatePortfolio(portfolio);

        } catch (NoSuchElementException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro ao resgatar saldo do portfólio: " + e.getMessage(), e);
        }
    }

    /** Realiza a compra de uma criptomoeda em um Portfolio.
     * @param userID Identificador do usuario cujo Portfolio sera utilizado.
     * @param portfolioID Identificador do Portfolio do usuario.
     * @param cryptoName Nome da criptomoeda que sera comprada.
     * @param amount Quantidade da criptomoeda que sera comprada.
     * @throws IOException Caso ocorra um erro na leitura dos Portfolios ou das criptomoedas no arquivo.
     * @throws IllegalArgumentException Caso alguma entrada seja invalida.
     * @throws NoSuchElementException Caso a criptomoeda ou o Portfolio nao seja encontrado.
     */
    public void buyCrypto(String userID, String portfolioID, String cryptoName, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Quantidade para compra deve ser maior do que zero");

        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
            CryptoCurrency crypto = loadCryptoByName(cryptoName);
            if(crypto.getAvailableAmount() < amount)
                throw new IllegalArgumentException("Quantidade dísponivel da criptomoeda é insuficiente para essa compra");

            double totalCost = amount * crypto.getPrice();
            if (portfolio.getBalance() < totalCost)
                throw new IllegalArgumentException("Saldo disponível não é suficiente para essa compra");

            portfolio.setBalance(portfolio.getBalance() - totalCost);

            if (hasCrypto(cryptoName, portfolio)) {
                Investment updatedInvestment = findInvestment(portfolio, cryptoName);
                double newPurchasePrice = (crypto.getPrice() * amount + updatedInvestment.getCryptoInvestedQuantity() * updatedInvestment.getPurchasePrice())
                        / (updatedInvestment.getCryptoInvestedQuantity() + amount);
                updatedInvestment.setPurchasePrice(newPurchasePrice);
                updatedInvestment.setCryptoInvestedQuantity(updatedInvestment.getCryptoInvestedQuantity() + amount);
            }

            else {
                crypto.setInvestorsAmount(crypto.getInvestorsAmount() + 1);
                Investment newInvestment = new Investment(crypto, crypto.getPrice(), amount);
                portfolio.getInvestments().add(newInvestment);
            }

            crypto.setAvailableAmount(crypto.getAvailableAmount() - amount);
            cryptoRepository.updateCrypto(crypto);
            portfolioRepository.updatePortfolio(portfolio);
            saveBuyTransaction(portfolio.getUserId(), portfolio.getId(), new Investment(crypto, crypto.getPrice(), amount));

        } catch (NoSuchElementException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro ao comprar criptomoeda para o portfólio: " + e.getMessage(), e);
        }
    }
  
    /** Realiza a venda de uma criptomoeda em um Portfolio.
     * @param userID Identificador do usuario cujo Portfolio sera utilizado.
     * @param portfolioID Identificador do Portfolio do usuario.
     * @param cryptoName Nome da criptomoeda que sera vendida.
     * @param amount Quantidade da criptomoeda que sera vendida.
     * @throws IOException Caso ocorra um erro na leitura dos Portfolios ou das criptomoedas no arquivo.
     * @throws IllegalArgumentException Caso alguma entrada seja invalida.
     * @throws NoSuchElementException Caso a criptomoeda ou o Portfolio nao seja encontrado.
     */
    public void sellCrypto(String userID, String portfolioID, String cryptoName, double amount) {
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);

            CryptoCurrency crypto = loadCryptoByName(cryptoName);

            if (!hasCrypto(cryptoName, portfolio))
                throw new IllegalArgumentException("Criptomoeda não encontrada no portfólio: " + cryptoName);

            if (amount <= 0)
                throw new IllegalArgumentException("Quantidade para venda deve ser maior do que zero");

            if (portfolio.getAssetAmount(cryptoName) < amount)
                throw new IllegalArgumentException("Quantidade da criptomoeda no portfólio é insuficiente");

            portfolio.setBalance(portfolio.getBalance() + amount * crypto.getPrice());
            Investment updatedInvestment = findInvestment(portfolio, cryptoName);

            if (updatedInvestment.getCryptoInvestedQuantity() - amount == 0) {
                crypto.setInvestorsAmount(crypto.getInvestorsAmount() - 1);
                portfolio.getInvestments().remove(updatedInvestment);
            }

            else
                updatedInvestment.setCryptoInvestedQuantity(updatedInvestment.getCryptoInvestedQuantity() - amount);

            crypto.setAvailableAmount(crypto.getAvailableAmount() + amount);
            cryptoRepository.updateCrypto(crypto);
            portfolioRepository.updatePortfolio(portfolio);
            saveSellTransaction(portfolio.getUserId(), portfolio.getId(), new Investment(crypto, crypto.getPrice(), amount));

        } catch (NoSuchElementException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro ao vender criptomoeda do portfólio: " + e.getMessage(), e);
        }
    }
}