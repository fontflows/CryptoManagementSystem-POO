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
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true));
        writer.write(crypto.toString() + "\n");
        writer.close();
    }

    public List<CryptoCurrency> loadCryptos() throws IOException {
        List<CryptoCurrency> cryptos = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
        String line;
        while ((line = reader.readLine()) != null) {
            cryptos.add(new CryptoCurrency(line.split(",")[0], Double.parseDouble(line.split(",")[1])));
        }
        reader.close();
        return cryptos;
    }
}