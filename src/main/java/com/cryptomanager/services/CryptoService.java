package com.cryptomanager.services;

import com.cryptomanager.exceptions.CryptoServiceException;
import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

import static com.cryptomanager.repositories.CryptoRepository.loadCryptoByName;
import static com.cryptomanager.repositories.TransactionsRepository.loadTransactions;

@Service
public class CryptoService {

    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);
    private final CryptoRepository cryptoRepository;
    private final TransactionsRepository transactionsRepository;

    @Autowired
    public CryptoService(CryptoRepository cryptoRepository, TransactionsRepository transactionsRepository) {
        this.cryptoRepository = cryptoRepository;
        this.transactionsRepository = transactionsRepository;
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

    public List<String> getAllCryptosToString(){
        try{
            return cryptoRepository.loadCryptosToString();
        } catch (IOException e) {
            throw new CryptoServiceException("Erro ao carregar criptomoedas",e);
        }
    }

    public CryptoCurrency getCryptoByName(String name) {
        try {
            return loadCryptoByName(name);
        } catch (IOException e) {
            logger.error("Erro ao carregar criptomoeda", e);
            throw new CryptoServiceException("Erro interno do servidor ao carregar criptomoeda" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao carregar criptomoeda", e);
            throw new CryptoServiceException("Erro ao carregar criptomoeda: " + e.getMessage(), e);
        }
    }

    public void addCrypto(String cryptoName, double price, double growthRate, int riskFactor, double availableAmount) {
        try {
            cryptoName = cryptoName.toUpperCase();
            CryptoCurrency newCrypto = new CryptoCurrency(cryptoName, price, growthRate, riskFactor, availableAmount);
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

    public void updateCrypto(String cryptoName, String fieldToEdit, String newValue) {
        try {
            CryptoCurrency crypto = loadCryptoByName(cryptoName);
            if(crypto.getInvestorsAmount() > 0) { throw new IllegalArgumentException("Criptomoeda tem investidores ativos e não pode ser editada"); }
            switch (fieldToEdit) {
                case "Price":
                    crypto.setPrice(validateParseDouble(newValue));
                    break;
                case "Growth Rate":
                    crypto.setGrowthRate(validateParseDouble(newValue));
                    break;
                case "Risk Factor":
                    crypto.setRiskFactor(validateParseInt(newValue));
                    break;
            }
            cryptoRepository.updateCrypto(crypto);
        } catch (IOException e) {
            logger.error("Erro ao atualizar criptomoeda", e);
            throw new CryptoServiceException("Erro interno do servidor ao atualizar criptomoeda" , e);
        } catch (IllegalArgumentException | NoSuchElementException e){
            logger.error("Erro ao atualizar criptomoeda", e);
            throw new CryptoServiceException("Erro ao atualizar criptomoeda: " + e.getMessage(), e);
        }
    }

    private static double validateParseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inserido tem formato inválido para o campo");
        }
    }

    private int validateParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inserido tem formato inválido para o campo");
        }
    }

    public static double calculateVolume24h(String cryptoName) throws IOException {
        List<String> history = loadTransactions("ALL");
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = localDateTime.format(formatter);
        double volume24h = 0.0;
        for(String transactions : history){
            String[] parts = transactions.split(",");
            if(parts[0].equals(date) && parts[4].equalsIgnoreCase(cryptoName) && parts.length == 7){
                volume24h += validateParseDouble(parts[5])*validateParseDouble(parts[6]);
            }
            else if(parts[0].equals(date) && (parts[4].equalsIgnoreCase(cryptoName) || parts[5].equalsIgnoreCase(cryptoName)) && parts.length == 9){
                volume24h += validateParseDouble(parts[8]);
            }
        }
        return volume24h;
    }
}
