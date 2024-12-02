package com.cryptomanager.models;

/**
 * Enumeracao responsavel por padronizar a nomenclatura dos tipos de estrategia de investimentos a serem ofertados no sistema Swagger.
 */
public enum StrategyNames {
    AGGRESSIVE("AGGRESSIVE"),
    MODERATE("MODERATE"),
    CONSERVATIVE("CONSERVATIVE");

    private final String displayName;

    StrategyNames(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}