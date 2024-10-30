package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;
import com.cryptomanager.models.Portfolio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InvestmentReportService{

    Portfolio portfolio;

    public InvestmentReportService(Portfolio portfolio) {
        this.portfolio = portfolio;
    }


}