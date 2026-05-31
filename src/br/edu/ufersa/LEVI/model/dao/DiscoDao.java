package br.edu.ufersa.LEVI.model.dao;

import br.edu.ufersa.LEVI.model.entity.Disco;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscoDao implements BaseDao<Disco> {

    @Override
    public Disco inserir(Disco disco) {
        String sql = "INSERT INTO disco (titulo, banda, estilo, ano, exemplares, valor_aluguel) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, disco.getTitulo());
            stmt.setString(2, disco.getBanda());
            stmt.setString(3, disco.getEstilo());
            stmt.setDate(4, disco.getAno() != null ? Date.valueOf(disco.getAno()) : null);
            stmt.setInt(5, disco.getExemplares());
            stmt.setFloat(6, disco.getValorAluguel());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    disco.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return disco;
    }

    @Override
    public Disco alterar(Disco disco) {
        String sql = "UPDATE disco SET titulo = ?, banda = ?, estilo = ?, ano = ?, exemplares = ?, valor_aluguel = ? WHERE id = ?";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, disco.getTitulo());
            stmt.setString(2, disco.getBanda());
            stmt.setString(3, disco.getEstilo());
            stmt.setDate(4, disco.getAno() != null ? Date.valueOf(disco.getAno()) : null);
            stmt.setInt(5, disco.getExemplares());
            stmt.setFloat(6, disco.getValorAluguel());
            stmt.setInt(7, disco.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return disco;
    }

    @Override
    public Disco deletar(Disco disco) {
        String sql = "DELETE FROM disco WHERE id = ?";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, disco.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return disco;
    }

    @Override
    public List<Disco> buscar(String parametro) {
        List<Disco> lista = new ArrayList<>();
        String sql = "SELECT * FROM disco WHERE titulo LIKE ? OR banda LIKE ? OR estilo LIKE ?";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String busca = "%" + parametro + "%";
            stmt.setString(1, busca);
            stmt.setString(2, busca);
            stmt.setString(3, busca);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Disco d = new Disco();
                    d.setId(rs.getInt("id"));
                    d.setTitulo(rs.getString("titulo"));
                    d.setBanda(rs.getString("banda"));
                    d.setEstilo(rs.getString("estilo"));
                    if (rs.getDate("ano") != null) {
                        d.setAno(rs.getDate("ano").toLocalDate());
                    }
                    d.setExemplares(rs.getInt("exemplares"));
                    d.setValorAluguel(rs.getFloat("valor_aluguel"));
                    lista.add(d);
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
    public List<Disco> listar() {
        List<Disco> lista = new ArrayList<>();
        String sql = "SELECT * FROM disco";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Disco d = new Disco();
                d.setId(rs.getInt("id"));
                d.setTitulo(rs.getString("titulo"));
                d.setBanda(rs.getString("banda"));
                d.setEstilo(rs.getString("estilo"));
                if (rs.getDate("ano") != null) {
                    d.setAno(rs.getDate("ano").toLocalDate());
                }
                d.setExemplares(rs.getInt("exemplares"));
                d.setValorAluguel(rs.getFloat("valor_aluguel"));
                lista.add(d);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return lista;
    }
}