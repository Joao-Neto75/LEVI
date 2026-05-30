package br.edu.ufersa.LEVI.model.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface BaseDao <E> {
    String URL = "jdbc:mysql:";
    String USER = "";   
    String PASSWORD = "";
    // colocar o nome do banco de dados no final da url
    final static String URL = "jdbc:mysql://localhost:3306/";

    // colocar o user local do banco de dados
    final static String USER = "";

    // colocar a senha do banco de dados
    final static String PASSWORD = "";

    final static Connection con = getConnection();


    public static Connection getConnection() {
            try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            // retorna o erro se o banco estiver desligado ou a senha de acesso esteja errada
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        return null;
    }


    public static void closeConnection() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                // retorna erro em caso de falha ao fechar uma comunicação
                throw new RuntimeException("Erro ao fechar a conexão com o banco de dados!");
            }
        }
    }


    public E inserir(E entity);
    public E deletar(E entity);
    public E alterar(E entity);
    public ResultSet buscar(String parametro);
    public ResultSet listar();









}