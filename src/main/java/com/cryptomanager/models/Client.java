package com.cryptomanager.models;

public class Client {
    private final String ClientID;
    private Portfolio portfolio;
    private String password;
    private final String role;


    public Client(String ClientID, Portfolio portfolio, String password, String role) {
        this.ClientID = ClientID;
        this.portfolio = portfolio;
        this.password = password;
        this.role = role;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getClientID() {
        return ClientID;
    }
    public String getRole() { return role; }

    @Override
    public String toString() {
        return ClientID + "," + portfolio.getId() + "," + password + "," + role ;
    }
}
