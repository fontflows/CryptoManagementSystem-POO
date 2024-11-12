package com.cryptomanager.repositories;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.services.CryptoService;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class CryptoRepository {

    private static final String FILE_PATH = "cryptos.txt";

    public void saveCrypto(CryptoCurrency crypto) throws IOException {
        if(cryptoExists(crypto.getName())) { throw new IllegalArgumentException("Criptomoeda ja foi cadastrada"); }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(crypto.toString() + "\n");
        }
    }

    public List<CryptoCurrency> loadCryptos() throws IOException {
        List<CryptoCurrency> cryptos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    cryptos.add(new CryptoCurrency(parts[0], Double.parseDouble(parts[1]),Double.parseDouble(parts[2]),Double.parseDouble(parts[3]),Double.parseDouble(parts[4]), Integer.parseInt(parts[5])));
                }
            }
        }
        if(cryptos.isEmpty()){ throw new NoSuchElementException("Nenhuma criptomoeda encontrada"); }
        return cryptos;
    }

    public CryptoCurrency loadCryptoByName(String cryptoName) throws IOException {
        CryptoCurrency crypto = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6 && parts[0].equalsIgnoreCase(cryptoName)) {
                    crypto = new CryptoCurrency(parts[0], Double.parseDouble(parts[1]),Double.parseDouble(parts[2]),Double.parseDouble(parts[3]),Double.parseDouble(parts[4]), Integer.parseInt(parts[5]));
                }
            }
        }
        if (crypto == null) { throw new NoSuchElementException("Criptomoeda não encontrada"); }
        return crypto;
    }

    public void deleteCryptoByName(String cryptoName) throws IOException {
        if(!cryptoExists(cryptoName)) { throw new NoSuchElementException("Criptomoeda não encontrada"); }
        List<CryptoCurrency> cryptos = loadCryptos();
        CryptoCurrency removedCrypto = null;
        for(CryptoCurrency crypto: cryptos) {
            if (crypto.getName().equalsIgnoreCase(cryptoName)) {
                removedCrypto = crypto;
                break;
            }
        }
        cryptos.remove(removedCrypto);
        // Reescreve o arquivo com a lista atualizada
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (CryptoCurrency crypto : cryptos) {
                writer.write(crypto.toString() + "\n");
            }
        }
    }

    public void updateCrypto(CryptoCurrency crypto) throws IOException {
        if(crypto == null || crypto.getName() == null ) { throw new IllegalArgumentException("Criptomoeda inválida");}
        deleteCryptoByName(crypto.getName());
        saveCrypto(crypto);
    }

    private boolean cryptoExists(String cryptoName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6 && parts[0].equalsIgnoreCase(cryptoName)) {
                    return true;
                }
            }
        }
        return false;
    }
}

