package com.cryptomanager.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConservativeStrategy implements InvestmentStrategy{
    private List<CryptoCurrency> conservativeCryptos;

    public ConservativeStrategy(List<CryptoCurrency> conservativeCryptos) {
        this.conservativeCryptos = conservativeCryptos != null ? conservativeCryptos : new ArrayList<>();
    }

    @Override
    public void setCryptos(List<CryptoCurrency> conservativeCryptos) {
        this.conservativeCryptos = conservativeCryptos;
    }

    @Override
    public List<CryptoCurrency> getCryptos() {
        return conservativeCryptos;
    }

    @Override
    public void addCryptoCurrency(CryptoCurrency crypto) {
        if(crypto == null) { throw new IllegalArgumentException("Crypto não pode ser nula"); }
        conservativeCryptos.add(crypto);
    }

    @Override
    public void removeCryptoCurrency(CryptoCurrency crypto) {
        if(crypto == null) { throw new IllegalArgumentException("Crypto não pode ser nula"); }
        conservativeCryptos.remove(crypto);
    }

    @Override
    public CryptoCurrency getRandomCrypto() {
        Random rng = new Random();
        return conservativeCryptos.get(rng.nextInt(conservativeCryptos.size()));
    }
}
