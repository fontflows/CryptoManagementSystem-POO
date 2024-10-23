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
                if (parts.length == 5) {
                    cryptos.add(new CryptoCurrency(parts[0], Double.parseDouble(parts[1]),Double.parseDouble(parts[2]),Double.parseDouble(parts[3]),Double.parseDouble(parts[4])));
                }
            }
        }
        return cryptos;
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
}
