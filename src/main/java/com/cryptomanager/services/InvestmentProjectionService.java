package com.cryptomanager.services;

public class InvestmentProjectionService {

    //Calcula o patrimonio projetado baseado na taxa de crescimento mensal e quantidade de meses investido
    public static double calculateInvestmentProjection (int coinAmount, double price, double growthRate, int months){
        if(months < 0) { throw new IllegalArgumentException("Quantidade de meses nao pode ser negativa"); }
        if (coinAmount < 0) { throw new IllegalArgumentException("Quantidade de moedas nao pode ser negativa"); }
        if (price < 0) { throw new IllegalArgumentException("Preco nao pode ser negativo"); }
        if (growthRate <= -1) { throw new IllegalArgumentException("Taxa de crescimento invalida"); }

        double totalValue = coinAmount*price;
        totalValue = totalValue*Math.pow(1+growthRate, months);
        return totalValue;
    }

    //Calcula o tempo em meses para atingir um valor alvo baseado na taxa de crescimento mensal
    public static int calculateTimeToTarget (int coinAmount, double price, double growthRate, double targetValue){
        if (coinAmount <= 0) { throw new IllegalArgumentException("Quantidade de moedas invalida"); }
        if (price <= 0) { throw new IllegalArgumentException("Preco invalido"); }
        if (targetValue <= 0) { throw new IllegalArgumentException("Valor alvo invalido"); }
        if (growthRate <= -1) { throw new IllegalArgumentException("Taxa de crescimento invalida"); }

        double totalValue = coinAmount*price;
        if((targetValue < totalValue && growthRate > 0) || (targetValue > totalValue && growthRate < 0)) {
            throw new IllegalArgumentException("Valor alvo incompativel com taxa de crescimento");
        }
        double time = Math.log(targetValue / totalValue) / Math.log(1 + growthRate);
        return (int) Math.ceil(time);
    }
}
