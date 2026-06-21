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
        String sql = "INSERT INTO funcionarios (nome, cargo, salario, contratacao, email, senha) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, entity.getNome());
            stmt.setString(2, entity.getCargo());
            stmt.setDouble(3, entity.getSalario());
            stmt.setDate(4, new java.sql.Date(entity.getContratacao().getTime()));
            stmt.setString(5, entity.getEmail());
            stmt.setString(6, entity.getSenha());
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
        String sql = "UPDATE funcionarios SET nome=?, cargo=?, salario=?, contratacao=?, email=?, senha=? WHERE id=?";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, entity.getNome());
            stmt.setString(2, entity.getCargo());
            stmt.setDouble(3, entity.getSalario());
            stmt.setDate(4, new java.sql.Date(entity.getContratacao().getTime()));
            stmt.setString(5, entity.getEmail());
            stmt.setString(6, entity.getSenha());
            stmt.setInt(7, entity.getId());
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
                    lista.add(mapearFuncionario(resul));
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
                lista.add(mapearFuncionario(resul));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na listagem: " + e.getMessage());
        }
        return lista;
    }

    // Busca um único funcionário pelo e-mail, usado no momento do login
    public Funcionarios buscarPorEmail(String email) {
        String sql = "SELECT * FROM funcionarios WHERE email = ?";
        try (Connection con = BaseDao.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet resul = stmt.executeQuery()) {
                if (resul.next()) {
                    return mapearFuncionario(resul);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na busca por e-mail: " + e.getMessage());
        }
        return null;
    }

    // monta o objeto Funcionarios a partir de uma linha do ResultSet
    private Funcionarios mapearFuncionario(ResultSet resul) throws SQLException {
        Funcionarios f = new Funcionarios();
        f.setId(resul.getInt("id"));
        f.setNome(resul.getString("nome"));
        f.setCargo(resul.getString("cargo"));
        f.setSalario(resul.getDouble("salario"));
        f.setContratacao(resul.getDate("contratacao"));
        f.setEmail(resul.getString("email"));
        f.setSenha(resul.getString("senha"));
        return f;
    }

}