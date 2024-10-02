package com.cryptomanager.models;

import com.cryptomanager.repositories.PortifolioRepository;
import java.util.List;

public class Portifolio extends PortifolioRepository {
    private String id, userId, list;
    private List<Investiment> investimentos;
    private String codigo;
    private double valor;

    public Portifolio(String codigo, double valor) {
        this.codigo = codigo;
        this.valor = valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Portifolio that = (Portifolio) obj;
        return Double.compare(that.valor, valor) == 0 && codigo.equals(that.codigo);
    }

    public static double calculoTotalPortifolio(double tempo, double taxaAtualCrypto, double valorCompra, int totalCryptos) {
        Investiment investiment = new Investiment(tempo);

        investiment.setQuantidade(totalCryptos);
        investiment.setCryptoCurrency(taxaAtualCrypto);
        investiment.setPrecoCompra(valorCompra);
        investiment.setTempo(tempo);

        return investiment.modulacaoEPrestacaoParcelada();
    }

    public String toString() {
        return investimentos + "," + id + "," + userId + "," + list;
    }
}