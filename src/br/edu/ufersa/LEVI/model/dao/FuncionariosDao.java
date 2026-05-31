package br.edu.ufersa.LEVI.model.dao;
import br.edu.ufersa.LEVI.model.entity.Funcionarios;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionariosDao implements BaseDao<Funcionarios> {
    private static Connection con;
    

    public Funcionarios inserir (Funcionarios entity){
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

    public Funcionarios deletar (Funcionarios entity){
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

    public Funcionarios alterar (Funcionarios entity){
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

    
    public List<Funcionarios> buscar(String parametro) {
        String sql = "SELECT * FROM funcionarios WHERE nome LIKE ?";
        List<Funcionarios> lista = new ArrayList<>();

        
        try (Connection con = BaseDao.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            
            stmt.setString(1, "%" + parametro + "%"); 
            
            try (ResultSet resul = stmt.executeQuery()) {
                while (resul.next()) {
                    Funcionarios f = new Funcionarios();
                    f.setId(resul.getInt("id"));
                    f.setNome(resul.getString("nome"));
                    f.setCargo(resul.getString("cargo"));
                    f.setSalario(resul.getDouble("salario"));
                    f.setContratacao(resul.getDate("contratacao"));
                    
                    lista.add(f);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na busca: " + e.getMessage());
        }
        return lista; 
    }

    
    public List<Funcionarios> listar() {
        String sql = "SELECT * FROM funcionarios";
        List<Funcionarios> lista = new ArrayList<>();

        try (Connection con = BaseDao.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet resul = stmt.executeQuery()) {
            
            while (resul.next()) {
                Funcionarios f = new Funcionarios();
                f.setId(resul.getInt("id"));
                f.setNome(resul.getString("nome"));
                f.setCargo(resul.getString("cargo"));
                f.setSalario(resul.getDouble("salario"));
                f.setContratacao(resul.getDate("contratacao"));
                
                lista.add(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na listagem: " + e.getMessage());
        }
        return lista;
    }

}