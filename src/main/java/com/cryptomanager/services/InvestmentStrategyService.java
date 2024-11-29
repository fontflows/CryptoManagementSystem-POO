package com.cryptomanager.services;

import com.cryptomanager.models.*;
import com.cryptomanager.repositories.CryptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/** Classe responsavel pelos metodos Service das estrategias de investimento.*/
@Service
public class InvestmentStrategyService {

    private static CryptoRepository cryptoRepository;

    /** Constructor InvestmentStrategyService
     * @param cryptoRepository Instancia que conecta o Service a classe que manipula os dados das criptomoedas no arquivo.
     */
    @Autowired
    public InvestmentStrategyService(CryptoRepository cryptoRepository) {
        InvestmentStrategyService.cryptoRepository = cryptoRepository;
    }

    /** Atualiza a lista de CryptoCurrencies de uma dada estratégia de investimento baseado no fator de risco de cada moeda.
     * @param investmentStrategy Estratégia de investimento cuja lista de criptomoedas será atualizada.
     * @throws IOException Caso ocorra erros na leitura das criptomoedas no arquivo.
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

    /** Obtém uma instancia de uma CryptoCurrency baseado na estrategia de investimento selecionada.
     * @param investmentStrategy Estrategia de investimento da qual sera obtido uma criptomoeda.
     * @return {@code CryptoCurrency} criptomoeda sugerida.
     */
    public static CryptoCurrency getRandomCrypto(InvestmentStrategy investmentStrategy) {
        List<CryptoCurrency> cryptos = investmentStrategy.getSuggestedCryptos();
        Random rng = new Random();
        if (cryptos.isEmpty())
            throw new IllegalStateException("A lista de criptomoedas está vazia.");
        return cryptos.get(rng.nextInt(cryptos.size()));
    }
  
    /** Obtém uma instancia da classe InvestmentStrategy pelo nome da estrategia.
     * @param strategyName Nome da estrategia.
     * @return {@code InvestmentStrategy} Instancia de uma estrategia de investimento com nome identificado.
     */
    public static InvestmentStrategy getInvestmentStrategyByName(String strategyName) {
         switch (strategyName) {
            case "CONSERVATIVE": return new ConservativeStrategy();
            case "AGGRESSIVE": return new AggressiveStrategy();
            case "MODERATE": return new ModerateStrategy();
            default: throw new IllegalArgumentException("Estratégia de investimento inválida");
        }
    }
}
