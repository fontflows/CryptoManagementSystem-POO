package com.cryptomanager.models;

/**
 * Classe-filha responsavel por suportar a classe-pai InvestmentStrategy, com o tipo de estrategia moderada.
 */
public class ModerateStrategy extends InvestmentStrategy{
    public ModerateStrategy() {
         super("MODERATE", 2);
    }
}