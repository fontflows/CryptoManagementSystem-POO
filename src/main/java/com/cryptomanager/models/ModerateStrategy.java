package com.cryptomanager.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModerateStrategy implements InvestmentStrategy{

    private List<CryptoCurrency> moderateCryptos;

    public ModerateStrategy(List<CryptoCurrency> moderateCryptos) {
        this.moderateCryptos = moderateCryptos != null ? moderateCryptos : new ArrayList<>();
    }

    @Override
    public void setCryptos(List<CryptoCurrency> moderateCryptos) {
        this.moderateCryptos = moderateCryptos;
    }

    @Override
    public List<CryptoCurrency> getCryptos() {
        return moderateCryptos;
    }

    @Override
    public void addCryptoCurrency(CryptoCurrency crypto) {
        if(crypto == null) { throw new IllegalArgumentException("Crypto não pode ser nula"); }
        moderateCryptos.add(crypto);
    }

    @Override
    public void removeCryptoCurrency(CryptoCurrency crypto) {
        if(crypto == null) { throw new IllegalArgumentException("Crypto não pode ser nula"); }
        moderateCryptos.remove(crypto);
    }

    @Override
    public CryptoCurrency getRandomCrypto() {
        Random rng = new Random();
        return moderateCryptos.get(rng.nextInt(moderateCryptos.size()));
    }
}
