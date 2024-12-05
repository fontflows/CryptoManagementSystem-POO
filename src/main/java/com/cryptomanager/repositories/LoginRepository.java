package com.cryptomanager.repositories;

import org.springframework.stereotype.Repository;

import java.io.*;

/**Classe responsavel por lidar com a pertinencia de dados das informacoes do usuario logado*/
@Repository
public class LoginRepository {
    private static final String FILE_PATH = "loggedClient.txt";

    /** Metodo responsavel por armazenar os dados do usuario logado.
     * @param userID Recebe o userID do usuario logado.
     * @param portfolioID Recebe o portfolioID do usuario logado.
     * @throws IOException Caso ocorra algum erro de entrada/saida na manipulacao do arquivo.
     */
    public void saveLoggedInfo(String userID, String portfolioID) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(userID + "," + portfolioID);
        } catch (IOException e) {
            throw new IOException("Erro interno do servidor ao salvar os dados do usuario logado.");
        }
    }

    /** Metodo responsavel por ler os dados do usuario logado.
     * @return Retorna um array de String com dois elementos, contendo o userID e portfolioID respectivamente.
     * @throws IOException Caso ocorra algum erro de entrada/saida na manipulacao do arquivo.
     */
    public String[] loadLoggedInfo() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            line = reader.readLine();
            return line.split(",");
        }catch (IOException e) {
            throw new IOException("Erro interno do servidor ao carregar os dados do usuario logado.");
        }
    }

}
