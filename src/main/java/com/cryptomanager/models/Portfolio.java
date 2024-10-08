package com.cryptomanager.models;

import com.cryptomanager.repositories.PortifolioRepository;
import java.util.List;
import java.util.Scanner;

public class Portfolio extends PortifolioRepository {
    private String id, userId, listName; /*são respectivamente o id, o usuario e o nome da lista associados ao portifolio.
    uso ao longo do código, ou seja, voláteis às informações declaradas posteriormente pelo usário*/
    private List<Investment> investments; //lista dos investimentos realizados na plataforma
    private final String codigo; //codigo da criptomoeda
    private final double valor; //valor associado à criptomoeda
    private static Scanner sc;

    public Portfolio(String codigo, double valor) {
        this.codigo = codigo;
        this.valor = valor;
    }

    @Override
    public boolean equals(Object obj) { //verificador de portfolios
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Portfolio that = (Portfolio) obj;
        return Double.compare(that.valor, valor) == 0 && codigo.equals(that.codigo);
    }

    public static void preeencherPortfolio(String id, String userId, String listName){
        sc = new Scanner(System.in);
        System.out.print("ID da operacao: ");
        id = sc.nextLine();

        System.out.print("ID do usuario: ");
        userId = sc.nextLine();

        System.out.println("Nome da lista de execucao: "); /*aqui seria o nome dado a operacao que o usuário está
        realizando, como se salvasse o nome de cada investimento feito na plataforma de forma personalizada*/
        listName = sc.nextLine();

        System.out.println("Dados do portfolio preenchidos com sucesso!\n");
    }

    public static double calculoTotalPortifolio(double tempo, double taxaAtualCrypto, double valorCompra, int totalCryptos) {
        Investment investment = new Investment(tempo,taxaAtualCrypto, valorCompra, totalCryptos);
        return investment.modulacaoEPrestacaoParcelada();
    }

    public String toString() { //realiza a impressao dos dados relacionados ao portfolio de maneira ordenada
        return investments + "," + id + "," + userId + "," + listName;
    }
}