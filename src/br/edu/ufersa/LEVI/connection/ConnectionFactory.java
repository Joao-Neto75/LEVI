package br.edu.ufersa.LEVI.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // colocar o nome do banco de dados no final da url
    private static final String URL = "jdbc:mysql://localhost:3306/";

    // colocar o user local do banco de dados
    private static final String USER = "";

    // colocar a senha do banco de dados
    private static final String PASSWORD = "";


    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            // retorna o erro se o banco estiver desligado ou a senha de acesso esteja errada
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }
}