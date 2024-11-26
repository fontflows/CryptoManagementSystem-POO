package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;

/** Classe responsavel por calcular projecoes de investimento. */
public class InvestmentProjectionService {

    /** Calcula o patrimonio projetado baseado na taxa de crescimento mensal de uma criptomoeda e quantidade de meses investido
     * @param coinAmount Quantidade de criptomoedas consideradas na projecao.
     * @param months Quantidade de meses considerados na projecao.
     * @param cryptoCurrency Criptomoeda considerada na projecao.
     * @return {@code double} Patrimonio projetado apos periodo de tempo definido.
     * @throws IllegalArgumentException Caso valores dos parametros sejam invalidos.
     */
    public static double calculateInvestmentProjection (double coinAmount, CryptoCurrency cryptoCurrency, int months){
        double usedPrice = cryptoCurrency.getPrice();
        double usedGrowthRate = cryptoCurrency.getGrowthRate();

        if (months <= 0) { throw new IllegalArgumentException("Quantidade de meses nao pode ser negativa ou nula"); }
        if (coinAmount <= 0) { throw new IllegalArgumentException("Quantidade de moedas nao pode ser negativa ou nula"); }
        if (usedPrice <= 0) { throw new IllegalArgumentException("Preco nao pode ser negativo ou nulo"); }
        if (usedGrowthRate <= -1) { throw new IllegalArgumentException("Taxa de crescimento invalida"); }

        double totalValue = coinAmount*usedPrice;
        totalValue = totalValue*Math.pow(1+usedGrowthRate, months);
        return totalValue;
    }

    /** Calcula o tempo em meses para atingir um valor alvo baseado na taxa de crescimento mensal de uma criptomoeda
     * @param coinAmount Quantidade de criptomoedas consideradas na projecao.
     * @param targetValue Valor alvo de patrimonio desejado.
     * @param cryptoCurrency Criptomoeda considerada na projecao.
     * @return {@code int} Quantidade de meses necessarios para atingir valor alvo.
     * @throws IllegalArgumentException Caso valores dos parametros sejam invalidos.
     */
    public static int calculateTimeToTarget (double coinAmount, CryptoCurrency cryptoCurrency, double targetValue){
        double usedPrice = cryptoCurrency.getPrice();
        double usedGrowthRate = cryptoCurrency.getGrowthRate();

        if (coinAmount <= 0) { throw new IllegalArgumentException("Quantidade de moedas invalida"); }
        if (usedPrice <= 0) { throw new IllegalArgumentException("Preco invalido"); }
        if (targetValue <= 0) { throw new IllegalArgumentException("Valor alvo invalido"); }
        if (usedGrowthRate <= -1) { throw new IllegalArgumentException("Taxa de crescimento invalida"); }

        double totalValue = coinAmount*usedPrice;
        if((targetValue < totalValue && usedGrowthRate > 0) || (targetValue > totalValue && usedGrowthRate < 0)) {
            throw new IllegalArgumentException("Valor alvo incompativel com taxa de crescimento");
        }

        double time = Math.log(targetValue / totalValue) / Math.log(1 + usedGrowthRate);
        return (int) Math.ceil(time);
    }
}