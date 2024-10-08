package com.cryptomanager.models;

public class Investment {
        private double cryptoCurrency, precoCompra, tempoInvestment;
        private int quantidadeCryptosInvestidos;

        public Investment(double tempoInvestment, double cryptoCurrency, double precoCompra, int quantidadeCryptosInvestidos){
            this.cryptoCurrency = cryptoCurrency;
            this.precoCompra = precoCompra;
            this.quantidadeCryptosInvestidos = quantidadeCryptosInvestidos;
            this.tempoInvestment = tempoInvestment;
        }

        public double getTempoInvestment() {
            return tempoInvestment;
        }

        public double getQuantidadeCryptosInvestidos() {
            return quantidadeCryptosInvestidos;
        }

        public double getCryptoCurrency() {
            return cryptoCurrency;
        }

        public double getPrecoCompra() {
            return precoCompra;
        }

        public void setTempoInvestment(double tempoInvestment) {
            this.tempoInvestment = tempoInvestment;
        }

        public void setPrecoCompra(double precoCompra) {
            this.precoCompra = precoCompra;
        }

        public void setCryptoCurrency(double cryptoCurrency) {
            this.cryptoCurrency = cryptoCurrency;
        }

        public void setQuantidadeCryptosInvestidos(int quantidadeCryptosInvestidos){
            this.quantidadeCryptosInvestidos = quantidadeCryptosInvestidos;
        }

        public double modulacaoEPrestacaoParcelada(){
            return quantidadeCryptosInvestidos *(precoCompra*Math.pow(1 + cryptoCurrency, tempoInvestment) +
                    precoCompra/((Math.pow(1 + cryptoCurrency, tempoInvestment) - 1)/
                            (Math.pow(1 + cryptoCurrency, tempoInvestment))* tempoInvestment));

            /* A fórmula completa calcula o valor total de um investimento que é aumentado por um montante fixo a cada
            período (representando uma anuidade), além do crescimento devido aos juros compostos.
            Isso ilustra contextos onde você está investindo um valor inicial e fazendo contribuições adicionais ao
            longo do tempo.

            Partes:
            i) A expressão precoCompra * Math.pow(1 + cryptoCurrency, tempoInvestment) refere-se ao montante acumulado
            de um investimento com juros compostos. Aqui, precoCompra é o valor inicial investido, cryptoCurrency é a
            taxa de juros (ou retorno) por período e tempo é o número de períodos de capitalização.

            ii) A segunda parte, precoCompra/((Math.pow(1 + cryptoCurrency, tempo) - 1)/
            (Math.pow(1 + cryptoCurrency, tempo))*tempo), calcular o valor presente de uma série de pagamentos
            (anuidade) ao longo do tempo, onde os pagamentos são feitos em intervalos regulares. */
        }
    }