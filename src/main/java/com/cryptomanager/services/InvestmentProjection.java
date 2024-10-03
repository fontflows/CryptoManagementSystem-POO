package com.cryptomanager.services;

public class InvestmentProjection {

    public static double calculateInvestmentProjection(int coinAmount, double price, double growthRate, int months){
        double totalValue = coinAmount*price;
        //Calcula o valor total baseado na taxa de crescimento mensal e quantidade de meses investido
        totalValue = totalValue*Math.pow(1+growthRate, months);
        return totalValue;
    }

    public static int calculateTimeProjection(int coinAmount, double price, double growthRate, double targetValue){
        double totalValue = coinAmount*price;
        //Calcula o tempo em meses baseado na taxa de crescimento mensal
        double time = Math.log(targetValue/totalValue)/Math.log(1 + growthRate);
        return (int) Math.ceil(time);
    }
}
