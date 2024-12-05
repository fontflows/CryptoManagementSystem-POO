package com.cryptomanager.models;

/**
 * Enumeracao responsavel por padronizar a nomenclatura dos tipos de estrategia de investimentos a serem ofertados no sistema Swagger.
 */
public enum StrategyNames {
    /** Nome definido para estrategia "AGGRESSIVE".*/
    AGGRESSIVE("AGGRESSIVE"),
    /** Nome definido para estrategia "MODERATE".*/
    MODERATE("MODERATE"),
    /** Nome definido para estrategia "CONSERVATIVE".*/
    CONSERVATIVE("CONSERVATIVE");

    private final String displayName;

    /** Construtor StrategyName
     * @param displayName Recebe o nome da estrategia que foi selecionado.
     */
    StrategyNames(String displayName) {
        this.displayName = displayName;
    }

    /** Metodo responsavel por retornar o nome da estrategia selecionada.
     * @return Retorna o nome da estrategia.
     */
    public String getDisplayName() {
        return displayName;
    }
}