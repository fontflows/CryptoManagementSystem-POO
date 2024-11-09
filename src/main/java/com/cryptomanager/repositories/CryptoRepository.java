package com.cryptomanager.repositories;

import com.cryptomanager.models.CryptoCurrency;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CryptoRepository {

    private static final String FILE_PATH = "cryptos.txt";

    public void saveCrypto(CryptoCurrency crypto) throws IOException {
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
        return cryptos;
    }

    public List<String> loadCryptosToString() throws IOException{
        List<CryptoCurrency> cryptos = loadCryptos();
        List<String> stringOut = new ArrayList<>();
        for(CryptoCurrency crypto: cryptos){
            stringOut.add(crypto.toString());
        }
        return stringOut;
    }

    public CryptoCurrency loadCryptoByName(String  cryptoName) throws IOException {
        CryptoCurrency crypto = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6 && parts[0].equals(cryptoName)) {
                    crypto = new CryptoCurrency(parts[0], Double.parseDouble(parts[1]),Double.parseDouble(parts[2]),Double.parseDouble(parts[3]),Double.parseDouble(parts[4]), Integer.parseInt(parts[5]));
                }
            }
        }
        if (crypto == null) { throw new IllegalArgumentException("Criptomoeda não encontrada"); }
        return crypto;
    }

    public void deleteCryptoByName(String name) throws IOException {
        List<CryptoCurrency> cryptos = loadCryptos();
        cryptos.removeIf(crypto -> crypto.getName().equals(name));
        // Reescreve o arquivo com a lista atualizada
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (CryptoCurrency crypto : cryptos) {
                writer.write(crypto.toString() + "\n");
            }
        }
    }

    public void updateCrypto(CryptoCurrency crypto) throws IOException {
        List<CryptoCurrency> cryptos = loadCryptos();
        boolean found = false;
        for (CryptoCurrency cryptocurrency : cryptos) {
            if (cryptocurrency.getName().equals(crypto.getName())) {
                found = true;
                break;
            }
        }
        if(found){
            deleteCryptoByName(crypto.getName());
            saveCrypto(crypto);
        }
        else{
            throw new IllegalArgumentException();
        }
    }
}
