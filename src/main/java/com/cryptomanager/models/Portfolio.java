package com.cryptomanager.models;

import com.cryptomanager.repositories.PortifolioRepository;
import java.util.List;

public class Portfolio extends PortifolioRepository {
    private String id, userId, list;
    private List<Investment> investimentos;
    private String codigo;
    private double valor;

    public Portfolio(String codigo, double valor) {
        this.codigo = codigo;
        this.valor = valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Portfolio that = (Portfolio) obj;
        return Double.compare(that.valor, valor) == 0 && codigo.equals(that.codigo);
    }

    public static double calculoTotalPortifolio(double tempo, double taxaAtualCrypto, double valorCompra, int totalCryptos) {
        Investment investment = new Investment(tempo);

        investment.setQuantidade(totalCryptos);
        investment.setCryptoCurrency(taxaAtualCrypto);
        investment.setPrecoCompra(valorCompra);
        investment.setTempoInvestment(tempo);

        return investment.modulacaoEPrestacaoParcelada();
    }

    public String toString() {
        return investimentos + "," + id + "," + userId + "," + list;
    }
}