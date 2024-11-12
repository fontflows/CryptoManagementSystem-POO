package com.cryptomanager.services;

import com.cryptomanager.exceptions.CryptoServiceException;
import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.repositories.CryptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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
            throw new CryptoServiceException("Erro interno do servidor ao carregar criptomoedas" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao carregar criptomoedas", e);
            throw new CryptoServiceException("Erro ao carregar criptomoedas: " + e.getMessage(), e);
        }
    }

    public CryptoCurrency getCryptoByName(String name) {
        try {
            return cryptoRepository.loadCryptoByName(name);
        } catch (IOException e) {
            logger.error("Erro ao carregar criptomoeda", e);
            throw new CryptoServiceException("Erro interno do servidor ao carregar criptomoeda" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao carregar criptomoeda", e);
            throw new CryptoServiceException("Erro ao carregar criptomoeda: " + e.getMessage(), e);
        }
    }

    public void addCrypto(String cryptoName, double price, double growthRate, double marketCap, double volume24h, int riskFactor) {
        try {
            CryptoCurrency newCrypto = new CryptoCurrency(cryptoName, price, growthRate, marketCap, volume24h, riskFactor);
            cryptoRepository.saveCrypto(newCrypto);
        } catch (IOException e) {
            logger.error("Erro ao salvar criptomoeda", e);
            throw new CryptoServiceException("Erro interno do servidor ao salvar criptomoeda" , e);
        } catch (IllegalArgumentException e){
            logger.error("Erro ao salvar criptomoeda", e);
            throw new CryptoServiceException("Erro ao salvar criptomoeda: " + e.getMessage(), e);
        }
    }

    public void deleteCryptoByName(String name) {
        try {
            cryptoRepository.deleteCryptoByName(name);
        } catch (IOException e) {
            logger.error("Erro ao remover criptomoeda", e);
            throw new CryptoServiceException("Erro interno do servidor ao remover criptomoeda" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao remover criptomoeda", e);
            throw new CryptoServiceException("Erro ao remover criptomoeda: " + e.getMessage(), e);
        }
    }

    public void updateCrypto(CryptoCurrency crypto) {
        try {
            cryptoRepository.updateCrypto(crypto);
        } catch (IOException e) {
            logger.error("Erro ao atualizar criptomoeda", e);
            throw new CryptoServiceException("Erro interno do servidor ao atualizar criptomoeda" , e);
        } catch (IllegalArgumentException e){
            logger.error("Erro ao atualizar criptomoeda", e);
            throw new CryptoServiceException("Erro ao atualizar criptomoeda: " + e.getMessage(), e);
        }
    }
}
