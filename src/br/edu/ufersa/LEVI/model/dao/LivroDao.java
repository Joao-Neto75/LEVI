package br.edu.ufersa.LEVI.model.dao;

import br.edu.ufersa.LEVI.model.entity.Livro;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LivroDao extends AbstractDao<Livro> {

    @Override
    protected Livro executarInsercao(Connection con, Livro livro) throws SQLException {
        String sql = "INSERT INTO livro (titulo, genero, ano, autor, paginas, exemplares, valor_aluguel) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
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
        }
        return livro;
    }

    @Override
    protected Livro executarAlteracao(Connection con, Livro livro) throws SQLException {
        String sql = "UPDATE livro SET titulo = ?, genero = ?, ano = ?, autor = ?, paginas = ?, exemplares = ?, valor_aluguel = ? WHERE id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getGenero());
            stmt.setDate(3, livro.getAno() != null ? Date.valueOf(livro.getAno()) : null);
            stmt.setString(4, livro.getAutor());
            stmt.setInt(5, livro.getPaginas());
            stmt.setInt(6, livro.getExemplares());
            stmt.setFloat(7, livro.getValorAluguel());
            stmt.setInt(8, livro.getId());

            stmt.executeUpdate();
        }
        return livro;
    }

    @Override
    protected Livro executarExclusao(Connection con, Livro livro) throws SQLException {
        String sql = "DELETE FROM livro WHERE id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, livro.getId());
            stmt.executeUpdate();
        }
        return livro;
    }

    @Override
    protected List<Livro> executarBusca(Connection con, String parametro) throws SQLException {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT * FROM livro WHERE titulo LIKE ? OR autor LIKE ? OR genero LIKE ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            String busca = "%" + parametro + "%";
            stmt.setString(1, busca);
            stmt.setString(2, busca);
            stmt.setString(3, busca);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLivro(rs));
                }
            }
        }
        return lista;
    }

    @Override
    protected List<Livro> executarListagem(Connection con) throws SQLException {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT * FROM livro";

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLivro(rs));
            }
        }
        return lista;
    }

    private Livro mapearLivro(ResultSet rs) throws SQLException {
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
        return l;
    }
}