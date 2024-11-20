package com.cryptomanager.repositories;

import com.cryptomanager.models.CryptoCurrency;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.cryptomanager.services.CryptoService.calculateVolume24h;


@Repository
public class CryptoRepository {

    private static final String FILE_PATH = "cryptos.txt";

    public void saveCrypto(CryptoCurrency crypto) throws IOException {
        if(cryptoExists(crypto.getName())) { throw new IllegalArgumentException("Criptomoeda ja foi cadastrada"); }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(crypto + "\n");
        }
    }

    public List<CryptoCurrency> loadCryptos() throws IOException {
        List<CryptoCurrency> cryptos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 9) {
                    CryptoCurrency loadedCrypto = new CryptoCurrency(parts[0], Double.parseDouble(parts[1]),Double.parseDouble(parts[2]), Integer.parseInt(parts[5]), Double.parseDouble(parts[7]));
                    loadedCrypto.setMarketCap(Double.parseDouble(parts[3]));
                    loadedCrypto.setVolume24h(Double.parseDouble(parts[4]));
                    loadedCrypto.setInvestorsAmount(Integer.parseInt(parts[6]));
                    loadedCrypto.setAvailableAmount(Double.parseDouble(parts[8]));
                    cryptos.add(loadedCrypto);
                }
            }
        }
        if(cryptos.isEmpty()){ throw new NoSuchElementException("Nenhuma criptomoeda encontrada"); }
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

    public static CryptoCurrency loadCryptoByName(String cryptoName) throws IOException {
        CryptoCurrency crypto = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 9 && parts[0].equalsIgnoreCase(cryptoName)) {
                    crypto = new CryptoCurrency(parts[0], Double.parseDouble(parts[1]),Double.parseDouble(parts[2]), Integer.parseInt(parts[5]), Double.parseDouble(parts[7]));
                    crypto.setMarketCap(Double.parseDouble(parts[3]));
                    crypto.setVolume24h(Double.parseDouble(parts[4]));
                    crypto.setInvestorsAmount(Integer.parseInt(parts[6]));
                    crypto.setAvailableAmount(Double.parseDouble(parts[8]));
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
                if(removedCrypto.getInvestorsAmount() > 0) { throw new IllegalArgumentException("A criptomoeda tem investidores ativos e não pode ser removida"); }
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

    public void updateCrypto(CryptoCurrency updatedCrypto) throws IOException {
        if(updatedCrypto == null || updatedCrypto.getName() == null ) { throw new IllegalArgumentException("Criptomoeda inválida");}
        if(!cryptoExists(updatedCrypto.getName())) { throw new NoSuchElementException("Criptomoeda não encontrada"); }
        List<CryptoCurrency> allCryptos = loadCryptos();
        updatedCrypto.setVolume24h(calculateVolume24h(updatedCrypto.getName()));
        updatedCrypto.setMarketCap(updatedCrypto.getPrice()*updatedCrypto.getTotalAmount());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (CryptoCurrency crypto : allCryptos) {
                if(crypto.getName().equalsIgnoreCase(updatedCrypto.getName())){
                    writer.write(updatedCrypto.toString());
                    writer.newLine();
                }
                else {
                    writer.write(crypto.toString());
                    writer.newLine();
                }
            }
        }
    }

    private boolean cryptoExists(String cryptoName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 9 && parts[0].equalsIgnoreCase(cryptoName)) {
                    return true;
                }
            }
        }
        return false;
    }
}

