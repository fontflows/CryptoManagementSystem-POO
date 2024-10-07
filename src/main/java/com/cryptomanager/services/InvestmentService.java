package com.cryptomanager.services;

import com.cryptomanager.repositories.CryptoRepository;

public class InvestmentService extends CryptoService{

    public InvestmentService(CryptoRepository cryptoRepository) {
        super(cryptoRepository);
    }
}