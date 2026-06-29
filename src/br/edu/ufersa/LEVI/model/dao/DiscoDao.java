package br.edu.ufersa.LEVI.model.dao;

import br.edu.ufersa.LEVI.model.entity.Disco;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscoDao extends AbstractDao<Disco> {

    @Override
    protected Disco executarInsercao(Connection con, Disco disco) throws SQLException {
        String sql = "INSERT INTO disco (titulo, banda, estilo, ano, exemplares, valor_aluguel) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
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
        }
        return disco;
    }

    @Override
    protected Disco executarAlteracao(Connection con, Disco disco) throws SQLException {
        String sql = "UPDATE disco SET titulo = ?, banda = ?, estilo = ?, ano = ?, exemplares = ?, valor_aluguel = ? WHERE id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, disco.getTitulo());
            stmt.setString(2, disco.getBanda());
            stmt.setString(3, disco.getEstilo());
            stmt.setDate(4, disco.getAno() != null ? Date.valueOf(disco.getAno()) : null);
            stmt.setInt(5, disco.getExemplares());
            stmt.setFloat(6, disco.getValorAluguel());
            stmt.setInt(7, disco.getId());

            stmt.executeUpdate();
        }
        return disco;
    }

    @Override
    protected Disco executarExclusao(Connection con, Disco disco) throws SQLException {
        String sql = "DELETE FROM disco WHERE id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, disco.getId());
            stmt.executeUpdate();
        }
        return disco;
    }

    @Override
    protected List<Disco> executarBusca(Connection con, String parametro) throws SQLException {
        List<Disco> lista = new ArrayList<>();
        String sql = "SELECT * FROM disco WHERE titulo LIKE ? OR banda LIKE ? OR estilo LIKE ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            String busca = "%" + parametro + "%";
            stmt.setString(1, busca);
            stmt.setString(2, busca);
            stmt.setString(3, busca);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDisco(rs));
                }
            }
        }
        return lista;
    }

    @Override
    protected List<Disco> executarListagem(Connection con) throws SQLException {
        List<Disco> lista = new ArrayList<>();
        String sql = "SELECT * FROM disco";

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDisco(rs));
            }
        }
        return lista;
    }

    private Disco mapearDisco(ResultSet rs) throws SQLException {
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
        return d;
    }
}