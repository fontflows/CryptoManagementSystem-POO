package com.cryptomanager.repositories;

import org.springframework.stereotype.Repository;

import java.io.*;

@Repository
public class LoginRepository {

    public static final String FILE_PATH = "loggedClient.txt";

    public void saveLoggedInfo(String userID, String portfolioID) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(userID + "," + portfolioID);
        }
    }

    public String[] loadLoggedInfo() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            line = reader.readLine();
            return line.split(",");
        }
    }

}