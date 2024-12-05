package com.cryptomanager.models;

/**
 * Classe modelo da estrutura padrao do cliente a ser cadastrado/editado no sistema Swagger.
 */
public class Client {
    private final String ClientID;
    private Portfolio portfolio;
    private String password;
    private String role;


    /** Construtor padrao da classe Client.
     * @param ClientID Recebe o Id do cliente.
     * @param portfolio Recebe o portfolio em usa estrutura.
     * @param password Recebe a senha associada ao cliente.
     * @param role Recebe a funcao do cliente no sistema, definindo suas permissoes.
     */
    public Client(String ClientID, Portfolio portfolio, String password, String role) {
        this.ClientID = ClientID;
        this.portfolio = portfolio;
        this.password = password;
        this.role = role;
    }

    /** Metodo responsavel por obter o portfolio associado ao cliente.
     * @return Retorna o portfolio.
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    /** Metodo responsavel por atribuir o portfolio do cliente.
     * @param portfolio Recebe o portfolio.
     */
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    /** Metodo responsavel por obter a senha associada ao cliente.
     * @return Retorna a senha associada ao cliente.
     */
    public String getPassword() {
        return password;
    }

    /** Metodo responsavel por atribuir a senha ao cliente cadastrado/editado.
     * @param password Recebe a senha associada ao cliente.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Metodo responsavel por obter o id associado ao cliente.
     * @return Retorna o Id associado ao cliente.
     */
    public String getClientID() {
        return ClientID;
    }

    /** Metodo responsavel por atribuir o Role ao cliente cadastrado/editado.
     * @param role Recebe o Role associada ao cliente.
     */
    public void setRole(String role) { this.role = role; }

    /** Metodo responsavel por obter o Role associado ao cliente.
     * @return Retorna o Role associado ao cliente.
     */
    public String getRole() { return role; }

    /** Metodo que sobrepoe a funcionalidade padrão toString()
     * @return Retorna a impressão das informações, de maneira formatada.
     */
    @Override
    public String toString() {
        return ClientID + "," + portfolio.getId() + "," + password + "," + role ;
    }
}