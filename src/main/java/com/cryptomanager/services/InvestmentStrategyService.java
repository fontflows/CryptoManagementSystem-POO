package com.cryptomanager.services;

import com.cryptomanager.models.*;
import com.cryptomanager.repositories.CryptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/** Classe responsável pelos métodos Service das estratégias de investimento.*/
@Service
public class InvestmentStrategyService {

    private static CryptoRepository cryptoRepository;

    /** Constructor InvestmentStrategyService
     * @param cryptoRepository Instância que conecta o Service à classe que manipula os dados das CryptoCurrencies no arquivo.
     */
    @Autowired
    public InvestmentStrategyService(CryptoRepository cryptoRepository) {
        InvestmentStrategyService.cryptoRepository = cryptoRepository;
    }

    /** Atualiza a lista de CryptoCurrencies de uma dada estratégia de investimento baseado no fator de risco de cada moeda.
     * @param investmentStrategy Estratégia de investimento cuja lista de CryptoCurrencies será atualizada.
     * @throws IOException Caso ocorra erros na leitura das CryptoCurrencies no arquivo.
     */
    public static void updateInvestmentStrategyList(InvestmentStrategy investmentStrategy) throws IOException {
        investmentStrategy.getSuggestedCryptos().clear();
        List<CryptoCurrency> cryptos = cryptoRepository.loadCryptos();
        for(CryptoCurrency crypto : cryptos){
            if(crypto.getRiskFactor() <= investmentStrategy.getRiskQuota()) {
                investmentStrategy.getSuggestedCryptos().add(crypto);
            }
        }
    }

    /** Obtém uma CryptoCurrency baseado na estratégia de investimento selecionada.
     * @param investmentStrategy Estratégia de investimento da qual será sugerido uma CryptoCurrency.
     * @return {@code CryptoCurrency} Moeda sugerida.
     */
    public static CryptoCurrency getRandomCrypto(InvestmentStrategy investmentStrategy) {
        List<CryptoCurrency> cryptos = investmentStrategy.getSuggestedCryptos();
        Random rng = new Random();
        if (cryptos.isEmpty())
            throw new IllegalStateException("A lista de criptomoedas está vazia.");
        return cryptos.get(rng.nextInt(cryptos.size()));
    }

    /** Obtém uma instância da classe InvestmentStrategy pelo nome da estratégia.
     * @param strategyName Nome da estratégia.
     * @return {@code InvestmentStrategy} Instância de uma estratégia de investimento com nome identificado.
     */
    public static InvestmentStrategy getInvestmentStrategyByName(String strategyName) {
         switch (strategyName) {
            case "Conservative": return new ConservativeStrategy();
            case "Aggressive": return new AggressiveStrategy();
            case "Moderate": return new ModerateStrategy();
            default: throw new IllegalArgumentException("Estratégia de investimento inválida");
        }
    }
}
