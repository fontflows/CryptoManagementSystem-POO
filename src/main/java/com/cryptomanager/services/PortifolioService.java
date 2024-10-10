package com.cryptomanager.services;

import com.cryptomanager.repositories.CryptoRepository;

public class PortifolioService extends CryptoService{
    public PortifolioService(CryptoRepository cryptoRepository) {
        super(cryptoRepository);
    }
}