package com.cryptomanager.models;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {
    private String id; // ID do portfólio
    private String userId; // ID do usuário
    private List<String> list; // Lista de ativos no formato "nome,quantidade"

    // Construtor
    public Portfolio(String id, String userId) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("portfolioId não pode ser nulo ou vazio.");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId não pode ser nulo ou vazio.");
        }

        this.id = id;
        this.userId = userId;
        this.list = new ArrayList<>(); // Inicializa a lista de ativos
    }

    // Adiciona um ativo ao portfólio
    public void addAsset(String assetName, double amount) {
        if (assetName == null || assetName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do ativo não pode ser nulo ou vazio.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Quantidade de ativo não pode ser negativa.");
        }
        this.list.add(assetName + "," + amount); // Armazena como "nome,quantidade"
    }

    // Método para obter a quantidade de um ativo específico
    public Double getAssetAmount(String assetName) {
        for (String asset : list) {
            String[] parts = asset.split(",");
            if (parts.length == 2 && parts[0].equals(assetName)) {
                try {
                    return Double.parseDouble(parts[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter a quantidade do ativo: " + e.getMessage());
                }
            }
        }
        return null; // Retorna null se o ativo não for encontrado
    }

    // Método para validar se um ativo existe no portfólio
    public boolean hasAsset(String assetName) {
        for (String asset : list) {
            String[] parts = asset.split(",");
            if (parts.length == 2 && parts[0].equals(assetName)) {
                return true; // Ativo encontrado
            }
        }
        return false; // Ativo não encontrado
    }

    // Método para obter a lista de ativos no portfólio
    public List<String> getAssets() {
        return new ArrayList<>(list); // Retorna uma cópia da lista de ativos
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getList() { // Retorna a lista de ativos
        return new ArrayList<>(list); // Retorna uma cópia da lista
    }

    // Método toString para representar o portfólio em formato de string
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(userId);
        for (String asset : list) {
            sb.append(",").append(asset);
        }
        return sb.toString();
    }

    // Método equals para comparação
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Portfolio)) return false;
        Portfolio other = (Portfolio) obj;
        return id.equals(other.id) && userId.equals(other.userId);
    }

    // Método hashCode para consistência em coleções
    @Override
    public int hashCode() {
        return 31 * id.hashCode() + userId.hashCode();
    }
}