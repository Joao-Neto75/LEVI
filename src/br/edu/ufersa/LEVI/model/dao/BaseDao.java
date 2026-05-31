package br.edu.ufersa.LEVI.model.dao;
import java.sql.Connection;
import br.edu.ufersa.LEVI.connection.ConnectionFactory;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;


public interface BaseDao <E> {

    public static Connection getConnection() {
        return ConnectionFactory.getConnection();
    }

    public static void closeConnection() {
        ConnectionFactory.closeConnection();
    }


    public E inserir(E entity);
    public E deletar(E entity);
    public E alterar(E entity);
    public List<E> buscar(String parametro);
    public List<E> listar();


}