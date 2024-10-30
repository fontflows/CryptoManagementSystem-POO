package com.cryptomanager.services;

import com.cryptomanager.exceptions.InvestmentReportExceptions;
import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.repositories.CryptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Service
public class CryptoService {

    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);
    private final CryptoRepository cryptoRepository;

    @Autowired
    public CryptoService(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    public List<CryptoCurrency> getAllCryptos() {
        try {
            return cryptoRepository.loadCryptos();
        } catch (IOException e) {
            logger.error("Erro ao carregar criptomoedas", e);
            throw new InvestmentReportExceptions("Erro ao carregar criptomoedas", e);
        }
    }

    public void addCrypto(CryptoCurrency crypto) {
        try {
            cryptoRepository.saveCrypto(crypto);
        } catch (IOException e) {
            logger.error("Erro ao salvar criptomoeda: {}", crypto.getName(), e);
            throw new InvestmentReportExceptions("Erro ao salvar criptomoeda: " + crypto.getName(), e);
        }
    }

    public void deleteCryptoByName(String name) {
        try {
            cryptoRepository.deleteCryptoByName(name);
        } catch (Exception e) {
            logger.error("Erro ao deletar criptomoeda: {}", name, e);
            throw new InvestmentReportExceptions("Erro ao deletar criptomoeda", e);
        }
    }
}
