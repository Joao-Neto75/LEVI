package br.edu.ufersa.LEVI.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Implementa o padrão Singleton: garante que existe no máximo UMA instância
// desta classe controlando a conexão com o banco, em todo o programa.
//

public class ConnectionFactory {

    // colocar o nome do banco de dados no final da url
    private static final String URL = "jdbc:mysql://localhost:3306/levi";

    // colocar o user local do banco de dados
    private static final String USER = "root";

    // colocar a senha do banco de dados
    private static final String PASSWORD = "SUA_SENHA_AQUI";

    // única instância da classe, criada apenas quando for pedida pela primeira vez
    private static ConnectionFactory instancia;

    private Connection con;

    // construtor privado: impede "new ConnectionFactory()" fora desta classe
    private ConnectionFactory() {
    }

    public static ConnectionFactory getInstance() {
        if (instancia == null) {
            instancia = new ConnectionFactory();
        }
        return instancia;
    }

    public Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        return con;
    }

    public void closeConnection() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao fechar a conexão com o banco de dados!");
            } finally {
                con = null;
            }
        }
    }
}
