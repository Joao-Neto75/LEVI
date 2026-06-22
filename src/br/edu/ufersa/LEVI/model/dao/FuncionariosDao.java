package br.edu.ufersa.LEVI.model.dao;

import br.edu.ufersa.LEVI.connection.ConnectionFactory;
import br.edu.ufersa.LEVI.model.entity.Funcionarios;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionariosDao extends AbstractDao<Funcionarios> {

    @Override
    protected Funcionarios executarInsercao(Connection con, Funcionarios entity) throws SQLException {
        String sql = "INSERT INTO funcionarios (nome, cargo, salario, contratacao, email, senha) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getNome());
            stmt.setString(2, entity.getCargo());
            stmt.setDouble(3, entity.getSalario());
            stmt.setDate(4, new java.sql.Date(entity.getContratacao().getTime()));
            stmt.setString(5, entity.getEmail());
            stmt.setString(6, entity.getSenha());
            stmt.execute();

            try (ResultSet resul = stmt.getGeneratedKeys()) {
                if (resul.next()) {
                    entity.setId(resul.getInt(1));
                }
            }
        }
        return entity;
    }

    @Override
    protected Funcionarios executarAlteracao(Connection con, Funcionarios entity) throws SQLException {
        String sql = "UPDATE funcionarios SET nome=?, cargo=?, salario=?, contratacao=?, email=?, senha=? WHERE id=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, entity.getNome());
            stmt.setString(2, entity.getCargo());
            stmt.setDouble(3, entity.getSalario());
            stmt.setDate(4, new java.sql.Date(entity.getContratacao().getTime()));
            stmt.setString(5, entity.getEmail());
            stmt.setString(6, entity.getSenha());
            stmt.setInt(7, entity.getId());
            stmt.execute();
        }
        return entity;
    }

    @Override
    protected Funcionarios executarExclusao(Connection con, Funcionarios entity) throws SQLException {
        String sql = "DELETE FROM funcionarios WHERE id=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, entity.getId());
            stmt.execute();
        }
        return entity;
    }

    @Override
    protected List<Funcionarios> executarBusca(Connection con, String parametro) throws SQLException {
        String sql = "SELECT * FROM funcionarios WHERE nome LIKE ?";
        List<Funcionarios> lista = new ArrayList<>();

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, "%" + parametro + "%");

            try (ResultSet resul = stmt.executeQuery()) {
                while (resul.next()) {
                    lista.add(mapearFuncionario(resul));
                }
            }
        }
        return lista;
    }

    @Override
    protected List<Funcionarios> executarListagem(Connection con) throws SQLException {
        String sql = "SELECT * FROM funcionarios";
        List<Funcionarios> lista = new ArrayList<>();

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet resul = stmt.executeQuery()) {

            while (resul.next()) {
                lista.add(mapearFuncionario(resul));
            }
        }
        return lista;
    }

    // Método específico de FuncionariosDao, usado só no login. Não faz parte
    // do contrato BaseDao, então não segue o "molde" do Template Method — abre
    // e fecha a conexão manualmente, como fazia antes.
    public Funcionarios buscarPorEmail(String email) {
        String sql = "SELECT * FROM funcionarios WHERE email = ?";
        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet resul = stmt.executeQuery()) {
                if (resul.next()) {
                    return mapearFuncionario(resul);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na busca por e-mail: " + e.getMessage());
        } finally {
            ConnectionFactory.getInstance().closeConnection();
        }
        return null;
    }

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