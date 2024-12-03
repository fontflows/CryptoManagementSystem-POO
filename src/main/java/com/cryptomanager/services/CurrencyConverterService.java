package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;

import static com.cryptomanager.repositories.CryptoRepository.loadCryptoByName;
import static com.cryptomanager.repositories.TransactionsRepository.saveConversionTransaction;
import static com.cryptomanager.services.PortfolioService.findInvestment;
import static com.cryptomanager.services.PortfolioService.hasCrypto;

/**
 * Classe responsavel pelos metodos service de conversao das criptomoedas.
 */
@Service
public class CurrencyConverterService {
    private final PortfolioRepository portfolioRepository;
    private final CryptoRepository cryptoRepository;

    /** Construtor padrao da classe CurrencyConverterService.
     * @param portfolioRepository Instancia da classe PortfolioRepository, a qual lida com a pertinencid de dados do portfolio no sistema txt.
     * @param cryptoRepository Instancia da classe CryptoRepository, a qual lida com a pertinencid de dados do portfolio no sistema txt.
     */
    @Autowired
    public CurrencyConverterService(PortfolioRepository portfolioRepository, CryptoRepository cryptoRepository) {
        this.portfolioRepository = portfolioRepository;
        this.cryptoRepository = cryptoRepository;
    }

    /** Metodo responsavel por conduzir a conversao de dada criptomoeda para outra de interesse do usuario.
     * @param userId Recebe o ID do usuario.
     * @param portfolioId Recebe o ID do portfolio.
     * @param fromCrypto Recebe o nome da criptomoeda a ser convertida.
     * @param toCrypto Recebe o nome da criptomoeda a ser obtida, apos a conversao.
     * @param cryptoAmount Recebe a quantia desejada para ser convertida.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante o calculo do volume acumulado.
     * @throws NoSuchElementException Excecao lancada, caso o elemento detectado nao exista para o sistema.
     * @throws IllegalArgumentException Excecao lancada, caso o argumento detectado seja invalido para a execucao do metodo.
     */
    public void currencyConverter(String userId, String portfolioId, String fromCrypto, String toCrypto, double cryptoAmount) throws IOException {
        if (cryptoAmount <= 0)
            throw new IllegalArgumentException("Quantidade de criptomoedas a serem convertidas deve ser maior que zero");
        CryptoCurrency cryptoFrom = loadCryptoByName(fromCrypto);
        CryptoCurrency cryptoTo = loadCryptoByName(toCrypto);
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);

        if (portfolio == null)
            throw new NoSuchElementException("Portfólio não encontrado para o usuário " + userId);

        if (!hasCrypto(fromCrypto, portfolio))
            throw new NoSuchElementException("Criptomoeda " + fromCrypto + " não encontrada no portfólio");

        if (portfolio.getAssetAmount(fromCrypto) < cryptoAmount)
            throw new IllegalArgumentException("Quantidade da criptomoeda " + fromCrypto + " insuficiente no portfólio");

        Investment fromInvestment = findInvestment(portfolio, cryptoFrom.getName());
      
        // Obter a taxa de conversão
        double conversionRate = getConversionRate(cryptoFrom, cryptoTo);
        double newAmount = conversionRate * cryptoAmount;

        if(cryptoTo.getAvailableAmount() < newAmount)
            throw new IllegalArgumentException("Quantidade disponível da criptomoeda de destino é insuficiente para essa transação");
      
        // Atualizar o investimento de origem
        double fromOldAmount = fromInvestment.getCryptoInvestedQuantity();
        if (fromOldAmount - cryptoAmount == 0) {
            portfolio.getInvestments().remove(fromInvestment); // Remove o investimento se a quantidade se tornar zero
            cryptoFrom.setInvestorsAmount(cryptoFrom.getInvestorsAmount() - 1); // Decrementa o número de investidores
        }

        else
            fromInvestment.setCryptoInvestedQuantity(fromOldAmount - cryptoAmount); // Atualiza a quantidade

        // Se a criptomoeda de destino não existe no portfólio, cria um novo investimento
        if (!hasCrypto(toCrypto, portfolio)) {
            Investment newInvestment = new Investment(cryptoTo, cryptoTo.getPrice(), newAmount);
            portfolio.getInvestments().add(newInvestment); // Adiciona o novo investimento
            cryptoTo.setInvestorsAmount(cryptoTo.getInvestorsAmount() + 1); // Incrementa o número de investidores
        }

        // Se a criptomoeda já existe no portfólio, atualiza o investimento existente
        else {
            Investment toInvestment = findInvestment(portfolio, cryptoTo.getName());
            double toOldAmount = toInvestment.getCryptoInvestedQuantity();
            double averagePrice = (toInvestment.getPurchasePrice() * toOldAmount + cryptoTo.getPrice() * newAmount)
                    / (toOldAmount + newAmount); // Calcula o preço médio ponderado
            toInvestment.setCryptoInvestedQuantity(toOldAmount + newAmount); // Atualiza a quantidade total
            toInvestment.setPurchasePrice(averagePrice); // Atualiza o preço médio
        }

        cryptoFrom.setAvailableAmount(cryptoFrom.getAvailableAmount() + cryptoAmount);
        cryptoTo.setAvailableAmount(cryptoTo.getAvailableAmount() - newAmount);
        cryptoRepository.updateCrypto(cryptoFrom);
        cryptoRepository.updateCrypto(cryptoTo);
        portfolioRepository.updatePortfolio(portfolio);

        // Registrar a transação de conversão
        saveConversionTransaction(portfolio.getUserId(), portfolio.getId(), cryptoFrom.getName(), cryptoTo.getName(), cryptoAmount, conversionRate, cryptoAmount * cryptoFrom.getPrice());
    }

    /** Metodo estatico responsavel por obter a taxa de conversao entre as criptomoedas de interesse.
     * @param fromCrypto Recebe o nome da criptomoeda a ser convertida.
     * @param toCrypto Recebe o nome da criptomoeda a ser obtida, apos a conversao.
     * @return Retorna a taxa final calculada.
     */
    public static double getConversionRate(CryptoCurrency fromCrypto, CryptoCurrency toCrypto) {
        return fromCrypto.getPrice() / toCrypto.getPrice();
    }
}