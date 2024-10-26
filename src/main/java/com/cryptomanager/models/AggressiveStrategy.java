package com.cryptomanager.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AggressiveStrategy implements InvestmentStrategy{
    private List<CryptoCurrency> aggressiveCryptos;

    public AggressiveStrategy(List<CryptoCurrency> aggressiveCryptos) {
        this.aggressiveCryptos = aggressiveCryptos != null ? aggressiveCryptos : new ArrayList<>();
    }

    @Override
    public void setCryptos(List<CryptoCurrency> aggressiveCryptos) {
        this.aggressiveCryptos = aggressiveCryptos;
    }

    @Override
    public List<CryptoCurrency> getCryptos() {
        return aggressiveCryptos;
    }

    @Override
    public void addCryptoCurrency(CryptoCurrency crypto) {
        if(crypto == null) { throw new IllegalArgumentException("Crypto não pode ser nula"); }
        aggressiveCryptos.add(crypto);
    }

    @Override
    public void removeCryptoCurrency(CryptoCurrency crypto) {
        if(crypto == null) { throw new IllegalArgumentException("Crypto não pode ser nula"); }
        aggressiveCryptos.remove(crypto);
    }

    @Override
    public CryptoCurrency getRandomCrypto() {
        Random rng = new Random();
        if (aggressiveCryptos.isEmpty())
            throw new IllegalStateException("A lista de criptomoedas está vazia.");
        return aggressiveCryptos.get(rng.nextInt(aggressiveCryptos.size()));
    }
}
