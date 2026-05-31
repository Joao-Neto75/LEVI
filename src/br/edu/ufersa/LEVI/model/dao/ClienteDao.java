package br.edu.ufersa.LEVI.model.dao;

import br.edu.ufersa.LEVI.model.entity.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDao implements BaseDao<Cliente> {

    @Override
    public Cliente inserir(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, cpf, endereco) VALUES (?, ?, ?)";
        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEndereco());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return cliente;
    }

    @Override
    public Cliente alterar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, cpf = ?, endereco = ? WHERE id = ?";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEndereco());
            stmt.setInt(4, cliente.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return cliente;
    }

    @Override
    public Cliente deletar(Cliente cliente) {
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cliente.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return cliente;
    }

    @Override
    public List<Cliente> buscar(String parametro) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nome LIKE ? OR cpf LIKE ?";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String busca = "%" + parametro + "%";
            stmt.setString(1, busca);
            stmt.setString(2, busca);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setId(rs.getInt("id"));
                    c.setNome(rs.getString("nome"));
                    c.setCpf(rs.getString("cpf"));
                    c.setEndereco(rs.getString("endereco"));
                    lista.add(c);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return lista;
    }

    @Override
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                c.setCpf(rs.getString("cpf"));
                c.setEndereco(rs.getString("endereco"));
                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return lista;
    }
}