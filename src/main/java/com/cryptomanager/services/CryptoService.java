package com.cryptomanager.services;

import com.cryptomanager.exceptions.CryptoServiceException;
import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.repositories.CryptoRepository;
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

/**
 * Classe responsavel pelos metodos service das criptomoedas.
 */
@Service
public class CryptoService {

    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);
    private final CryptoRepository cryptoRepository;

    /** Construtor padrao da classe CryptoService.
     * @param cryptoRepository Instancia da classe responsavel por tratar as criptomoedas no sistema de arquivo txt.
     */
    @Autowired
    public CryptoService(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    /** Metodo responsavel por obter todas as criptomoedas presentes no sistema.
     * @return Retorna a lista das criptomoedas presentes no sistema.
     * @throws CryptoServiceException Excecao lancada, caso ocorra algum erro na execucao da funcionalidade da criptomoeda.
     */
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

    /** Metodo responsavel por obter todas as criptomoedas formatadas.
     * @return Retorna a lista de Strings das criptomoedas formatadas.
     * @throws CryptoServiceException Excecao lancada, caso ocorra algum erro na execucao da funcionalidade da criptomoeda.
     */
    public List<String> getAllCryptosToString(){
        try{
            return cryptoRepository.loadCryptosToString();
        } catch (IOException e) {
            throw new CryptoServiceException("Erro ao carregar criptomoedas",e);
        }
    }

    /** Metodo responsavel por obter a criptomoeda desejada, a partir do nome informado no sistema.
     * @param name Recebe o nome da criptomoeda de interesse.
     * @return Retorna a estrutura padrao da criptomoeda desejada.
     * @throws CryptoServiceException Excecao lancada, caso ocorra algum erro na execucao da funcionalidade da criptomoeda.
     */
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

    /** Metodo responsavel por adicionar dada criptomoeda informada no sistema.
     * @param cryptoName Recebe o nome da criptomoeda.
     * @param price Recebe o preco da criptomoeda.
     * @param growthRate Recebe a taxa de crescimento da criptomoeda.
     * @param riskFactor Recebe a taxa do fator de risco da criptomoeda no mercado.
     * @param availableAmount Recebe a quantia total da criptomoeda disponivel.
     * @throws CryptoServiceException Excecao lancada, caso ocorra algum erro na execucao da funcionalidade da criptomoeda.
     */
    public void addCrypto(String cryptoName, double price, double growthRate, int riskFactor, double availableAmount) {
        try {
            cryptoName = cryptoName.toUpperCase().trim();
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


    /** Metodo responsavel por remover certa criptomoeda do sistema, considerando o nome informado.
     * @param name Recebe o nome da criptomoeda.
     * @param reason Recebe a razao, devidamente especificada, do porque da remocao da criptomoeda do sistema.
     * @throws CryptoServiceException Excecao lancada, caso ocorra algum erro na execucao da funcionalidade da criptomoeda.
     */
    public void deleteCryptoByName(String name, String reason) {
        try {
            cryptoRepository.deleteCryptoByName(name);
            cryptoRepository.saveDeletionHistory(name, reason);
        } catch (IOException e) {
            logger.error("Erro ao remover criptomoeda", e);
            throw new CryptoServiceException("Erro interno do servidor ao remover criptomoeda" , e);
        } catch (NoSuchElementException | IllegalArgumentException e){
            logger.error("Erro ao remover criptomoeda", e);
            throw new CryptoServiceException("Erro ao remover criptomoeda: " + e.getMessage(), e);
        }
    }

    /** Metodo responsavel por atualizar a criptomoeda de interesse.
     * @param cryptoName Recebe o nome da criptomoeda.
     * @param fieldToEdit Recebe o campo de interesse a ser alterado na criptomoeda
     * @param newValue Recebe o novo valor a ser inserido, considerando o argumento informado em "fieldToEdit".
     * @throws CryptoServiceException Excecao lancada, caso ocorra algum erro na execucao da funcionalidade da criptomoeda.
     */
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

    /** Metodo estatico responsavel por validar a insercao de um valor, a partir de um encapsulamento para a classe Double.
     * @param value Recebe o valor a ser validado.
     * @return Retorna o valor encapsulado e validado para Double.
     * @throws IllegalArgumentException Excecao lancada, caso o argumento informado para o metodo seja invalido.
     */
    private static double validateParseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inserido tem formato inválido para o campo");
        }
    }

    /** Metodo responsavel por validar a insercao de um valor, a partir de um encapsulamento para a classe Integer.
     * @param value Recebe o valor a ser validado.
     * @return Retorna o valor encapsulado e validado para Integer.
     * @throws IllegalArgumentException Excecao lancada, caso o argumento informado para o metodo seja invalidado.
     */
    private int validateParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inserido tem formato inválido para o campo");
        }
    }

    /** Metodo responsavel por calcular o total acumulado de uma criptomoeda, durante um periodo de 24 horas.
     * @param cryptoName Recebe o nome da criptomoeda.
     * @return Retorna o total acumulado pela criptomoeda informada.
     * @throws IOException Excecao lancada, caso ocorra algum erro de entrada/saida durante o calculo do volume acumulado.
     */
    public static double calculateVolume24h(String cryptoName) throws IOException {
        List<String> history = loadTransactions("ALL");
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = localDateTime.format(formatter);
        double volume24h = 0.0;
        for(String transactions : history){
            String[] parts = transactions.split(",");
            if(parts[0].equals(date) && parts[4].equalsIgnoreCase(cryptoName.trim()) && parts.length == 7){
                volume24h += validateParseDouble(parts[5])*validateParseDouble(parts[6]);
            }
            else if(parts[0].equals(date) && (parts[4].equalsIgnoreCase(cryptoName.trim()) || parts[5].equalsIgnoreCase(cryptoName.trim())) && parts.length == 9){
                volume24h += validateParseDouble(parts[8]);
            }
        }
        return volume24h;
    }

    /** Metodo responsavel por obter o historico das criptomoedas removidas, devidamente, do sistema.
     * @return Retorna o historico das criptomoedas que foram removidas, considerando a motivacao especificada.
     * @throws CryptoServiceException Excecao lancada, caso ocorra algum erro na execucao da funcionalidade da criptomoeda.
     */
    public String getDeletedCryptosHistory(){
        try {
            String history = cryptoRepository.getDeletionHistoryToString();
            if(history.isEmpty()){
                throw new NoSuchElementException("Nenhuma remoção de criptomoeda salva no histórico");
            } else{
                return "| Histórico de Remoção de Criptomoedas |\n\n" + history;
            }
        } catch (IOException e) {
            logger.error("Erro interno do servidor ao obter histórico de criptomoedas removidas", e);
            throw new CryptoServiceException("Erro interno do servidor ao obter histórico de criptomoedas removidas" , e);
        } catch (NoSuchElementException e){
            logger.error("Erro ao obter histórico de criptomoedas removidas", e);
            throw new CryptoServiceException("Erro ao obter histórico de criptomoedas removidas: " + e.getMessage() , e);
        }
    }
}