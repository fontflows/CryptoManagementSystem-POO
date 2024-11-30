package com.cryptomanager.repositories;

import com.cryptomanager.models.*;
import com.cryptomanager.exceptions.*;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.cryptomanager.repositories.CryptoRepository.loadCryptoByName;

/**
 * Classe responsável por lidar com a pertinência de dados do portfólio dos usuários do sistema Swagger.
 */
@Repository
public class PortfolioRepository {
    private static final String FILE_PATH = "portfolio.txt";

    /** Método responsável por Adicionar um novo portfólio.
     * @param portfolio Recebe o objeto da classe Portfolio, o qual será adicionado no sistema txt.
     * @throws IOException Exceção lançada, caso alguma informação básica do portfólio não esteja adequada.
     */
    public void addPortfolio(Portfolio portfolio) throws IOException {
        if (!isValidPortfolio(portfolio))
            throw new PortfolioValidationException("Portfólio não válido.");

        savePortfolio(portfolio);
    }

    /** Método responsável por salvar um portfólio no arquivo.
     * @param portfolio Recebe o objeto da classe Portfolio, o qual será salvo no sistema txt. (Adição de um novo portfólio)
     * @throws IOException Exceção lançada, caso alguma informação básica do portfólio não esteja adequada.
     */
    private void savePortfolio(Portfolio portfolio) throws IOException {
        if (portfolioExists(portfolio.getId(), portfolio.getUserId()))
            throw new PortfolioAlreadyExistsException("Portfólio com esse ID já existe para o usuário: " + portfolio.getUserId());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))){
            writer.write(portfolio + "\n");
        }
    }

    /** Método responsável por carregar o portfólio especificado pelo ID do portfólio e do usuário.
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @throws PortfolioNotFoundException Exceção lançada, caso o portfólio associado/declarado não esteja no sistema de dados txt.
     * @return Retorna o portfólio especificado, devidamente carregado.
     */
    // Carrega o portfólio especificado por ID e userId
    public Portfolio loadPortfolioByUserIdAndPortfolioId(String userId, String portfolioId) {
        List<Portfolio> allPortfolios = loadAllPortfolios();

        for (Portfolio portfolio : allPortfolios) {
            if (portfolio.getUserId().equalsIgnoreCase(userId.trim()) && portfolio.getId().equalsIgnoreCase(portfolioId.trim()))
                return portfolio;
        }

        throw new PortfolioNotFoundException("Portfólio não encontrado para o usuário: " + userId + " e ID: " + portfolioId, null);
    }

    /** Método responsável por atualizar todos os portfólios presentes no arquivo txt "portfolio.txt".
     * @param updatedPortfolio Recebe um objeto da classe Porfolio, o qual será atualizado.
     * @throws IOException Exceção associada ao método, a qual é lançada, caso os dados de entrada estejam inadequados para o sistema.
     * @throws PortfolioNotFoundException Exceção lançada, caso o portfólio informado não esteja no sistema de dados txt.
     */
    public void updatePortfolio(Portfolio updatedPortfolio) throws IOException {
        if (!portfolioExists(updatedPortfolio.getUserId(), updatedPortfolio.getId()))
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + updatedPortfolio.getId(), null);

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

    /** Método responsável por carregar todos os portfólios do arquivo "portfolio.txt".
     * @throws PortfolioNotFoundException Exceção lançada, caso o portfólio informado não esteja no sistema de dados txt.
     * @return Retorna a lista de portfólios armazenada no txt.
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

    /** Método que possuí função auxiliar para criar um investimento, a partir de uma linha de texto.
     * @param parts Recebe o array de Strings, que contém as informações do portfólio na linha de texto.
     * @throws IOException Exceção lançada, caso alguma informação básica do portfólio não esteja adequada.
     * @return Retorna o investimento gerado/criado, a partir das informações capturadas.
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

    /** Método responsável pela validação de um portfólio.
     * @param portfolio Recebe o objeto da classe Portfolio, o qual terá sua existência verificada.
     * @return Retorna o valor booleano associado à sua existência ("true" ou "false").
     */
    public boolean isValidPortfolio(Portfolio portfolio) {
        if (portfolio == null)
            throw new PortfolioValidationException("Portfólio não pode ser nulo.");

        if (portfolio.getUserId() == null || portfolio.getUserId().isEmpty())
            throw new PortfolioValidationException("userId não pode ser nulo ou vazio.");

        if (portfolio.getId() == null || portfolio.getId().isEmpty())
            throw new PortfolioValidationException("portfolioId não pode ser nulo ou vazio.");

        return true;
    }

    /** Método responsável pela deleção do portfólio informado.
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @throws IOException Exceção lançada, caso alguma informação básica do portfólio não esteja adequada.
     */
    public void deletePortfolio(String userId, String portfolioId) throws IOException {
        if (!portfolioExists(userId, portfolioId))
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + portfolioId, null);

        if (portfolioHasInvestments(userId, portfolioId))
            throw new PortfolioHasInvestmentsException("Portfólio tem investimentos ativos, não pode ser excluído.");

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

    /** Método responsável por conferir a existência prévia de um portfólio construído no sistema Swagger, pelas informações do usuário.
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @throws IOException Exceção lançada, caso alguma informação básica do portfólio não esteja adequada.
     * @return Retorna o valor booleano associado à verificação.
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

    /** Método responsável por verificar se o portfólio informado é dotado de investimentos.
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @throws IOException Exceção lançada, caso alguma informação básica do portfólio não esteja adequada.
     * @return Retorna o valor booleano associado à verificação.
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

                else if (parts.length >= 8 && found)
                    return true;

                else if (parts.length == 4 && found)
                    return false;
            }
        }

        return false;
    }
}