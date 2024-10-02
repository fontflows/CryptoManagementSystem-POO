package com.cryptomanager.models;

public class Investiment {
        private double cryptoCurrency, precoCompra, tempo;
        private int quantidade;

        public Investiment(double tempo){
            this.tempo = tempo;
        }

        public double getTempo() {
            return tempo;
        }

        public double getQuantidade() {
            return quantidade;
        }

        public double getCryptoCurrency() {
            return cryptoCurrency;
        }

        public double getPrecoCompra() {
            return precoCompra;
        }

        public void setTempo(double tempo) {
            this.tempo = tempo;
        }

        public void setPrecoCompra(double precoCompra) {
            this.precoCompra = precoCompra;
        }

        public void setCryptoCurrency(double cryptoCurrency) {
            this.cryptoCurrency = cryptoCurrency;
        }

        public void setQuantidade(int quantidade){
            this.quantidade = quantidade;
        }

        public double modulacaoEPrestacaoParcelada(){
            return quantidade*(precoCompra*Math.pow(1 + cryptoCurrency, tempo) +
                    precoCompra/((Math.pow(1 + cryptoCurrency, tempo) - 1)/(Math.pow(1 + cryptoCurrency, tempo))*tempo));
            //cálculo usando noção de juros compostos conjuntamente de aplicação de prestações de investimento.
        }
    }