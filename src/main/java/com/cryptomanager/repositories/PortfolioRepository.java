package com.cryptomanager.repositories;

import com.cryptomanager.models.*;
import com.cryptomanager.exceptions.*;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.cryptomanager.repositories.CryptoRepository.loadCryptoByName;

/**
 * Classe responsavel por lidar com a pertinencia de dados do portfolio dos usuarios do sistema Swagger.
 */
@Repository
public class PortfolioRepository {
    private static final String FILE_PATH = "portfolio.txt";

    /** Metodo responsavel por Adicionar um novo portfolio.
     * @param portfolio Recebe o objeto da classe Portfolio, o qual sera adicionado no sistema txt.
     * @throws IOException Excecao lancada, caso ocorra alguma falha/interrupção em algum valor de entrada/saída.
     */
    public void addPortfolio(Portfolio portfolio) throws IOException {
        if (!isValidPortfolio(portfolio))
            throw new PortfolioValidationException("Portfolio não valido.");
        savePortfolio(portfolio);
    }

    /** Metodo responsavel por salvar um portfolio no arquivo.
     * @param portfolio Recebe o objeto da classe Portfolio, o qual sera salvo no sistema txt. (Adicao de um novo portfolio)
     * @throws IOException Excecao lancada, caso ocorra alguma falha/interrupção em algum valor de entrada/saída.
     */
    private void savePortfolio(Portfolio portfolio) throws IOException {
        if (portfolioExists(portfolio.getId(), portfolio.getUserId()))
            throw new PortfolioAlreadyExistsException("Portfolio com esse ID ja existe para o usuario: " + portfolio.getUserId());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))){
            writer.write(portfolio + "\n");
        }
    }

    /** Metodo responsavel por carregar o portfolio especificado pelo ID do portfolio e do usuario.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @throws PortfolioNotFoundException Excecao lancada, caso o portfolio associado/declarado nao esteja no sistema de dados txt.
     * @return Retorna o portfolio especificado, devidamente carregado.
     */
    // Carrega o portfolio especificado por ID e userId
    public Portfolio loadPortfolioByUserIdAndPortfolioId(String userId, String portfolioId) {
        List<Portfolio> allPortfolios = loadAllPortfolios();

        for (Portfolio portfolio : allPortfolios) {
            if (portfolio.getUserId().equalsIgnoreCase(userId.trim()) && portfolio.getId().equalsIgnoreCase(portfolioId.trim()))
                return portfolio;
        }

        throw new PortfolioNotFoundException("Portfolio não encontrado para o usuario: " + userId + " e ID: " + portfolioId, null);
    }

    /** Metodo responsavel por atualizar todos os portfolios presentes no arquivo txt "portfolio.txt".
     * @param updatedPortfolio Recebe um objeto da classe Portfolio, o qual sera atualizado.
     * @throws IOException Excecao lancada, caso ocorra alguma falha/interrupção em algum valor de entrada/saída.
     * @throws PortfolioNotFoundException Excecao lancada, caso o portfolio informado nao esteja no sistema de dados txt.
     */
    public void updatePortfolio(Portfolio updatedPortfolio) throws IOException {
        if (!portfolioExists(updatedPortfolio.getUserId(), updatedPortfolio.getId()))
            throw new PortfolioNotFoundException("Portfolio nao encontrado: " + updatedPortfolio.getId(), null);

        List<Portfolio> allPortfolios = loadAllPortfolios();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portfolio portfolio : allPortfolios) {
                if (updatedPortfolio.getId().equalsIgnoreCase(portfolio.getId().trim()))
                    writer.write(updatedPortfolio + "\n");
                else
                    writer.write(portfolio + "\n");
            }
        }
    }

    /** Metodo responsavel por carregar todos os portfolios do arquivo "portfolio.txt".
     * @throws PortfolioNotFoundException Excecao lancada, caso o portfolio informado nao esteja no sistema de dados txt.
     * @return Retorna a lista de portfolios armazenada no txt.
     */
    public List<Portfolio> loadAllPortfolios() {
        List<Portfolio> portfolioList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            Portfolio currentPortfolio = null;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length == 4) {
                    currentPortfolio = new Portfolio(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
                    portfolioList.add(currentPortfolio);
                }

                else if (parts.length == 3 && currentPortfolio != null) {
                    Investment investment = createInvestmentFromParts(parts);
                    currentPortfolio.getInvestments().add(investment);
                }
            }

        } catch (IOException e) {
            throw new PortfolioLoadException("Erro interno do servidor ao carregar portfólios", e);
        }

        return portfolioList;
    }

    /** Metodo que possui funcao auxiliar para criar um investimento, a partir de uma linha de texto.
     * @param parts Recebe o array de Strings, que contem as informacoes do portfolio na linha de texto.
     * @throws IOException Excecao lancada, caso ocorra alguma falha/interrupção em algum valor de entrada/saída.
     * @return Retorna o investimento gerado/criado, a partir das informacoes capturadas.
     */
    private Investment createInvestmentFromParts(String[] parts) throws IOException {
        if (parts.length < 3)
            throw new IllegalArgumentException("Dados do investimento mal formados");

        String cryptoName = parts[0];
        double quantity = Double.parseDouble(parts[1]);
        double purchasePrice = Double.parseDouble(parts[2]);

        CryptoCurrency cryptoCurrency = loadCryptoByName(cryptoName);

        return new Investment(cryptoCurrency, purchasePrice, quantity);
    }

    /** Metodo responsavel pela validacao de um portfolio.
     * @param portfolio Recebe o objeto da classe Portfolio, o qual tera sua existencia verificada.
     * @return Retorna o valor booleano associado a sua existencia ("true" ou "false").
     */
    public boolean isValidPortfolio(Portfolio portfolio) {
        if (portfolio == null)
            throw new PortfolioValidationException("Portfolio nao pode ser nulo.");

        if (portfolio.getUserId() == null || portfolio.getUserId().isEmpty())
            throw new PortfolioValidationException("userId nao pode ser nulo ou vazio.");

        if (portfolio.getId() == null || portfolio.getId().isEmpty())
            throw new PortfolioValidationException("portfolioId nao pode ser nulo ou vazio.");

        return true;
    }

    /** Metodo responsavel pela delecao do portfolio informado.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @throws IOException Excecao lancada, caso ocorra alguma falha/interrupção em algum valor de entrada/saída.
     */
    public void deletePortfolio(String userId, String portfolioId) throws IOException {
        if (!portfolioExists(userId, portfolioId))
            throw new PortfolioNotFoundException("Portfolio nao encontrado: " + portfolioId, null);

        if (portfolioHasInvestments(userId, portfolioId))
            throw new PortfolioHasInvestmentsException("Portfolio tem investimentos ativos, nao pode ser excluido.");

        List<Portfolio> portfolios = loadAllPortfolios();
        Portfolio removedPortfolio = null;

        for (Portfolio currentPortfolio : portfolios) {
            if (currentPortfolio.getUserId().equalsIgnoreCase(userId.trim()) && currentPortfolio.getId().equalsIgnoreCase(portfolioId.trim())) {
                removedPortfolio = currentPortfolio;
                break;
            }
        }

        portfolios.remove(removedPortfolio);

        // Reescreve o arquivo com a lista atualizada
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Portfolio currentPortfolio : portfolios)
                writer.write(currentPortfolio.toString() + "\n");
        }
    }

    /** Metodo responsavel por conferir a existencia previa de um portfolio construido no sistema Swagger, pelas informacoes do usuario.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @throws IOException Excecao lancada, caso ocorra alguma falha/interrupção em algum valor de entrada/saída.
     * @return Retorna o valor booleano associado a verificacao.
     */
    private boolean portfolioExists(String userId, String portfolioId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");

                if (parts.length == 4 && parts[0].equalsIgnoreCase(portfolioId.trim()) && parts[1].equalsIgnoreCase(userId.trim()))
                    return true;
            }
        }

        return false;
    }

    /** Metodo responsavel por verificar se o portfolio informado e dotado de investimentos.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @throws IOException Excecao lancada, caso ocorra alguma falha/interrupção em algum valor de entrada/saída.
     * @return Retorna o valor booleano associado a verificacao.
     */
    private boolean portfolioHasInvestments(String userId, String portfolioId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");

                if (parts.length == 4 && parts[0].equalsIgnoreCase(portfolioId.trim()) && parts[1].equalsIgnoreCase(userId.trim()))
                    found = true;
                
                else if(parts.length == 8 && found)
                    return true;

                else if (parts.length == 4 && found)
                    return false;
            }
        }

        return false;
    }
}