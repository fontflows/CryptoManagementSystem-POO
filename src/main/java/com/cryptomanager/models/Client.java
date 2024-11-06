package com.cryptomanager.models;

public class Client{
    private String ClientID;
    private Portfolio portfolio;
    private String password;


    public Client(String ClientID, Portfolio portfolio, String password) {
        this.ClientID = ClientID;
        this.portfolio = portfolio;
        this.password = password;
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

    @Override
    public String toString() {
        return ClientID + "," + portfolio.getId() + "," + password ;
    }
}
