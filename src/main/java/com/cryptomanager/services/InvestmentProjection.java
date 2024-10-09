package com.cryptomanager.services;

public class InvestmentProjection {

    public static double calculateInvestmentProjection(int coinAmount, double price, double growthRate, int months){
        if(months <= 0) { return coinAmount*price; }
        double totalValue = coinAmount*price;
        //Calcula o valor total baseado na taxa de crescimento mensal e quantidade de meses investido
        totalValue = totalValue*Math.pow(1+growthRate, months);
        return totalValue;
    }

    public static int calculateInvestmentProjectionTime(int coinAmount, double price, double growthRate, double targetValue){
        if(coinAmount <= 0 || price <= 0 || targetValue <= 0 || growthRate <= -1) { return 0; }
        double totalValue = coinAmount*price;
        if(targetValue < totalValue && growthRate > 0) { return 0; }
        //Calcula o tempo em meses baseado na taxa de crescimento mensal
        double time = Math.log(targetValue/totalValue)/Math.log(1 + growthRate);
        return (int) Math.ceil(time);
    }
}
