package br.edu.ufersa.LEVI.model.dao;

import br.edu.ufersa.LEVI.model.entity.Livro;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LivroDao implements BaseDao<Livro> {

    @Override
    public Livro inserir(Livro livro) {
        String sql = "INSERT INTO livro (titulo, genero, ano, autor, paginas, exemplares, valor_aluguel) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getGenero());
            stmt.setDate(3, livro.getAno() != null ? Date.valueOf(livro.getAno()) : null);
            stmt.setString(4, livro.getAutor());
            stmt.setInt(5, livro.getPaginas());
            stmt.setInt(6, livro.getExemplares());
            stmt.setFloat(7, livro.getValorAluguel());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    livro.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return livro;
    }

    @Override
    public Livro alterar(Livro livro) {
        String sql = "UPDATE livro SET titulo = ?, genero = ?, ano = ?, autor = ?, paginas = ?, exemplares = ?, valor_aluguel = ? WHERE id = ?";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getGenero());
            stmt.setDate(3, livro.getAno() != null ? Date.valueOf(livro.getAno()) : null);
            stmt.setString(4, livro.getAutor());
            stmt.setInt(5, livro.getPaginas());
            stmt.setInt(6, livro.getExemplares());
            stmt.setFloat(7, livro.getValorAluguel());
            stmt.setInt(8, livro.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return livro;
    }

    @Override
    public Livro deletar(Livro livro) {
        String sql = "DELETE FROM livro WHERE id = ?";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, livro.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return livro;
    }

    @Override
    public List<Livro> buscar(String parametro) {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT * FROM livro WHERE titulo LIKE ? OR autor LIKE ? OR genero LIKE ?";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String busca = "%" + parametro + "%";
            stmt.setString(1, busca);
            stmt.setString(2, busca);
            stmt.setString(3, busca);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Livro l = new Livro();
                    l.setId(rs.getInt("id"));
                    l.setTitulo(rs.getString("titulo"));
                    l.setGenero(rs.getString("genero"));
                    if (rs.getDate("ano") != null) {
                        l.setAno(rs.getDate("ano").toLocalDate());
                    }
                    l.setAutor(rs.getString("autor"));
                    l.setPaginas(rs.getInt("paginas"));
                    l.setExemplares(rs.getInt("exemplares"));
                    l.setValorAluguel(rs.getFloat("valor_aluguel"));
                    lista.add(l);
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
    public List<Livro> listar() {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT * FROM livro";

        try (Connection conn = BaseDao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Livro l = new Livro();
                l.setId(rs.getInt("id"));
                l.setTitulo(rs.getString("titulo"));
                l.setGenero(rs.getString("genero"));
                if (rs.getDate("ano") != null) {
                    l.setAno(rs.getDate("ano").toLocalDate());
                }
                l.setAutor(rs.getString("autor"));
                l.setPaginas(rs.getInt("paginas"));
                l.setExemplares(rs.getInt("exemplares"));
                l.setValorAluguel(rs.getFloat("valor_aluguel"));
                lista.add(l);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeConnection();
        }
        return lista;
    }
}