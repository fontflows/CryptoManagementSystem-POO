package com.cryptomanager.repositories;

import com.cryptomanager.models.CryptoCurrency;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.cryptomanager.services.CryptoService.calculateVolume24h;

/**
 * Classe responsavel por lidar com a pertinencia de dados das criptomoedas no sistema.
 */
@Repository
public class CryptoRepository {
    private static final String FILE_PATH = "cryptos.txt";
    private static final String DELETED_HISTORY_PATH = "cryptoDeletionHistory.txt";

    /** Metodo responsavel por salvar dada criptomoeda no arquivo "cryptos.txt".
     * @param crypto Instancia da classe padrao para a estrutura de qualquer criptomoeda do sistema.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante o salvamento da instancia.
     * @throws IllegalArgumentException Excecao lancada, caso seja decetado algum argumento invalido para a execucao do metodo.
     */
    public void saveCrypto(CryptoCurrency crypto) throws IOException {
        if(cryptoExists(crypto.getName())) { throw new IllegalArgumentException("Criptomoeda ja foi cadastrada"); }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(crypto + "\n");
        }
    }

    /** Metodo responsavel por carregar as criptomoedas salvas e presentes o arquivo "cryptos.txt".
     * @return Retorna a lista de criptomoedas existentes no sistema.
     * @throws IOException Excecao lacada, caso ocorra algum erro na entrada/saida durante o carregamento das criptomoedas existentes no sistema.
     * @throws NoSuchElementException Excecao lancada, caso o elemento detectado nao exista para o sistema.
     */
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

    /** Metodo responsavel por carregar e formatar as criptomoedas existentes no sistema.
     * @return Retorna a lista de Strings das criptomoedas formatadas
     * @throws IOException Excecao lancada, caso ocorrra algum erro de entrada/saida durante o carregamento/formatacao das criptomoedas.
     */
    public List<String> loadCryptosToString() throws IOException{
        List<CryptoCurrency> cryptos = loadCryptos();
        List<String> stringOut = new ArrayList<>();
        for(CryptoCurrency crypto: cryptos)
            stringOut.add(crypto.toString());

        return stringOut;
    }

    /** Metodo responsavel por carregar dada criptomoeda, a partir do nome informado.
     * @param cryptoName Recebe o nome da criptomoeda.
     * @return Retorna as informacoes da criptomoeda, considerando o nome informado.
     * @throws IOException Excecao lancada, caso, ocorra algum erro de entrada/saida durante o carregamento da criptomoeda, considerando o nome ofertado.
     * @throws NoSuchElementException Excecao lancada, caso o elemento detectado nao exista para o sistema.
     */
    public static CryptoCurrency loadCryptoByName(String cryptoName) throws IOException {
        CryptoCurrency crypto = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 9 && parts[0].equalsIgnoreCase(cryptoName.trim())) {
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

    /** Metodo responsavel por remover a criptomoeda, considerando o nome informado no sistema.
     * @param cryptoName Recebe o nome da criptomoeda.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a remocao da criptomoeda.
     * @throws NoSuchElementException Excecao lancada, caso o elemento detectado nao exista para o sistema.
     * @throws IllegalArgumentException Excecao lancada, caso o argumento detectado seja invalido para a execucao do metodo.
     */
    public void deleteCryptoByName(String cryptoName) throws IOException {
        if(!cryptoExists(cryptoName)) { throw new NoSuchElementException("Criptomoeda não encontrada"); }

        List<CryptoCurrency> cryptos = loadCryptos();
        CryptoCurrency removedCrypto = null;

        for(CryptoCurrency crypto: cryptos) {
            if (crypto.getName().equalsIgnoreCase(cryptoName.trim())) {
                removedCrypto = crypto;
                if(removedCrypto.getInvestorsAmount() > 0) { throw new IllegalArgumentException("A criptomoeda tem investidores ativos e não pode ser removida"); }
                break;
            }
        }

        cryptos.remove(removedCrypto);
        // Reescreve o arquivo com a lista atualizada
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (CryptoCurrency crypto : cryptos)
                writer.write(crypto.toString() + "\n");
        }
    }

    /** Metodo responsavel por atualizar certa criptomoeda do sistema, no arquivo "cryptos.txt".
     * @param updatedCrypto Instancia que recebe a criptomoeda a ser atualizada.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a atualizacao da criptomoeda especificada.
     * @throws NoSuchElementException Excecao lancada, caso o elemento detectado nao exista para o sistema.
     * @throws IllegalArgumentException Excecao lancada, caso o argumento detectado seja invalido para a execucao do metodo.
     */
    public void updateCrypto(CryptoCurrency updatedCrypto) throws IOException {
        if(updatedCrypto == null || updatedCrypto.getName() == null ) { throw new IllegalArgumentException("Criptomoeda inválida");}

        if(!cryptoExists(updatedCrypto.getName())) { throw new NoSuchElementException("Criptomoeda não encontrada"); }

        List<CryptoCurrency> allCryptos = loadCryptos();
        updatedCrypto.setVolume24h(calculateVolume24h(updatedCrypto.getName()));
        updatedCrypto.setMarketCap(updatedCrypto.getPrice()*updatedCrypto.getTotalAmount());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (CryptoCurrency crypto : allCryptos) {
                if(crypto.getName().equalsIgnoreCase(updatedCrypto.getName().trim())){
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

    /** Metodo responsavel por verificar se determinada criptomoeda existe no sistema.
     * @param cryptoName Recebe o nome da criptomoeda.
     * @return Retorna o valor booleano da verificacao (verdadeiro ou falso).
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante a verificacao.
     */
    private boolean cryptoExists(String cryptoName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 9 && parts[0].equalsIgnoreCase(cryptoName.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void saveDeletionHistory(String cryptoName, String reason) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DELETED_HISTORY_PATH, true))) {
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            writer.write("Data: " + localDateTime.format(dateTimeFormatter) + "\n" + "Criptomoeda: " + cryptoName + "\n" + "Motivo da remoção: " + reason + "\n");
            writer.newLine();
        }
    }

    public String getDeletionHistoryToString() throws IOException{
        StringBuilder history = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(DELETED_HISTORY_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                history.append(line).append("\n");
            }
        }
        return history.toString();
    }
}