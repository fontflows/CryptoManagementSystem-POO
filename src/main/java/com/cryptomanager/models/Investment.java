package com.cryptomanager.models;

/**
 * Classe modelo da estrutura padrão do investimento construido durante a execucao do sistema Swagger, para cada usuario cadastrado.
 */
public class Investment {
    private double purchasePrice;
    private CryptoCurrency cryptoCurrency;
    private double cryptoInvestedQuantity;

    /** Construtor padrao da classe Investment.
     * @param cryptoCurrency Recebe um objeto de CryptoCurrency, o qual engloba o investimento a ser tratado.
     * @param purchasePrice Recebe o preco de compra da criptomoeda associada.
     * @param cryptoInvestedQuantity Recebe a quantiade de criptomoedas investidas.
     */
    public Investment(CryptoCurrency cryptoCurrency, double purchasePrice, double cryptoInvestedQuantity) {
        this.cryptoCurrency = cryptoCurrency;
        this.purchasePrice = purchasePrice;
        this.cryptoInvestedQuantity = cryptoInvestedQuantity;
    }

    /** Metodo responsavel por informar o total de criptomoedas investidas.
     * @return Retorna a quantidade de criptomoedas investidas.
     */
    public double getCryptoInvestedQuantity() {
        return cryptoInvestedQuantity;
    }

    /** Metodo responsavel por retornar o objeto da classe CyrptoCurrency, o qual esta relacionado ao investimento a ser tratado.
     * @return Retorna o objeto cryptoCurrency.
     */
    public CryptoCurrency getCryptoCurrency() {
        return cryptoCurrency;
    }

    /** Metodo responsavel por retornar o valor de compra da criptomoeda envolvida.
     * @return Retorna o valor de compra associado da criptomoeda.
     */
    public double getPurchasePrice() {
        return purchasePrice;
    }

    /** Metodo responsavel por atribuir o preco de compra da criptomoeda.
     * @param purchasePrice Recebe o preco a ser atribuido a criptomoeda.
     */
    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    /** Metodo responsavel por atribuir o objeto de CryptoCurrency, considerando o investimento assoicado.
     * @param cryptoCurrency Recebe o objeto de CryptoCurrency, o qual sera atribuido ao investimento manipulado durante a execucao do sistema.
     */
    public void setCryptoCurrency(CryptoCurrency cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }

    /** Metodo responsavel por atribuir a quantidade de criptomoedas compradas, ou seja, investidas.
     * @param cryptoInvestedQuantity Recebe o total de criptomoedas que serao investidas, considerando o portfolio do usuario associado.
     */
    public void setCryptoInvestedQuantity(double cryptoInvestedQuantity) {
        this.cryptoInvestedQuantity = cryptoInvestedQuantity;
    }

    /** Método que sobrecarrega a funcionalidade padrão toString()
     * @return Retorna a impressão das informações, de maneira formatada.
     */
    @Override
    public String toString() {
        return cryptoCurrency.getName() + "," + cryptoInvestedQuantity + "," + purchasePrice; //Formato: nomeCrypto, quantidadeInvestida, precoCompra
    }
}