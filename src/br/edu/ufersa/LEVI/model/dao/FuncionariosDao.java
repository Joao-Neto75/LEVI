package br.edu.ufersa.LEVI.model.dao;
import br.edu.ufersa.LEVI.model.entity.Funcionarios;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FuncionariosDao implements BaseDao<Funcionarios> {
    private static Connection con;
    

    public FuncionariosDao inserir (Funcionarios entity){
        con = BaseDao.getConnection();
        String sql = "INSERT INTO funcionarios (nome, cargo, salario, contratacao)" + "VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, entity.getNome());
            stmt.setString(2, entity.getCargo());
            stmt.setDouble(3, entity.getSalario());
            stmt.setDate(4, new java.sql.Date(entity.getContratacao().getTime()));
            stmt.execute();
            ResultSet resul = stmt.getGeneratedKeys();
            if (resul.next()) {
                int id = resul.getInt(1);
                entity.setId(id);
            }

            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro na inserção: " + e.getMessage());
        }
        return entity;
        
    }

    public FuncionaiosDao deletar (Funcionarios entity){
        con = BaseDao.getConnection();
        String sql = "DELETE FROM funcionarios WHERE id=?";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, entity.getId());
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro na exclusão: " + e.getMessage());
        }
        return entity;
    }

    public FuncionariosDao alterar (Funcionarios entity){
        con = BaseDao.getConnection();
        String sql = "UPDATE funcionarios SET nome=?, cargo=?, salario=?, contratacao=? WHERE id=?";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, entity.getNome());
            stmt.setString(2, entity.getCargo());
            stmt.setDouble(3, entity.getSalario());
            stmt.setDate(4, new java.sql.Date(entity.getContratacao().getTime()));
            stmt.setInt(5, entity.getId());
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro na atualização: " + e.getMessage());
        }
        return entity;
    }

    public ResultSet buscar(String parametro) {
        con = BaseDao.getConnection();
        String sql = "SELECT * FROM funcionarios AS f WHERE  f.Nome=?";
        ResultSet resul = null;
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, parametro);
            resul = stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Erro na busca: " + e.getMessage());
        }
        return resul;
    }

    public ResultSet listar() {
        con = BaseDao.getConnection();
        String sql = "SELECT * FROM funcionarios";
        ResultSet resul = null;
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            resul = stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Erro na listagem: " + e.getMessage());
        }
    }

}