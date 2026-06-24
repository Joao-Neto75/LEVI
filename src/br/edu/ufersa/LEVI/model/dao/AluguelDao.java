package br.edu.ufersa.LEVI.model.dao;

import br.edu.ufersa.LEVI.connection.ConnectionFactory;
import br.edu.ufersa.LEVI.model.entity.Aluguel;
import br.edu.ufersa.LEVI.model.entity.Cliente;
import br.edu.ufersa.LEVI.model.entity.Disco;
import br.edu.ufersa.LEVI.model.entity.Livro;
import br.edu.ufersa.LEVI.model.entity.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AluguelDao extends AbstractDao<Aluguel> {

    @Override
    protected Aluguel executarInsercao(Connection con, Aluguel entity) throws SQLException {
        String sql = "INSERT INTO alugueis (cliente_id, data_emprestimo, data_devolucao, valor_total) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entity.getCliente().getId());
            stmt.setDate(2, Date.valueOf(entity.getDataEmprestimo()));
            stmt.setDate(3, entity.getDataDevolucao() != null ? Date.valueOf(entity.getDataDevolucao()) : null);
            stmt.setFloat(4, entity.getValorTotal());
            stmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getInt(1));
                }
            }
        }

        inserirProdutosDoAluguel(con, entity);
        return entity;
    }

    private void inserirProdutosDoAluguel(Connection con, Aluguel entity) throws SQLException {
        String sql = "INSERT INTO aluguel_produtos (aluguel_id, produto_id) VALUES (?, ?)";
        for (Produto p : entity.getProdutos()) {
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setInt(1, entity.getId());
                stmt.setInt(2, p.getId());
                stmt.execute();
            }
        }
    }

    @Override
    protected Aluguel executarAlteracao(Connection con, Aluguel entity) throws SQLException {
        String sql = "UPDATE alugueis SET data_devolucao=?, valor_total=? WHERE id=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, entity.getDataDevolucao() != null ? Date.valueOf(entity.getDataDevolucao()) : null);
            stmt.setFloat(2, entity.getValorTotal());
            stmt.setInt(3, entity.getId());
            stmt.execute();
        }
        return entity;
    }

    @Override
    protected Aluguel executarExclusao(Connection con, Aluguel entity) throws SQLException {
        try (PreparedStatement stmt1 = con.prepareStatement("DELETE FROM aluguel_produtos WHERE aluguel_id=?")) {
            stmt1.setInt(1, entity.getId());
            stmt1.execute();
        }
        try (PreparedStatement stmt2 = con.prepareStatement("DELETE FROM alugueis WHERE id=?")) {
            stmt2.setInt(1, entity.getId());
            stmt2.execute();
        }
        return entity;
    }

    @Override
    protected List<Aluguel> executarBusca(Connection con, String parametro) throws SQLException {
        // Busca por CPF ou nome do cliente, já hidratando o objeto Cliente e os Produtos
        String sql =
            "SELECT a.id, a.data_emprestimo, a.data_devolucao, a.valor_total, " +
            "       c.id AS c_id, c.nome AS c_nome, c.cpf AS c_cpf, c.endereco AS c_end " +
            "FROM alugueis a " +
            "JOIN cliente c ON a.cliente_id = c.id " +
            "WHERE c.cpf LIKE ? OR c.nome LIKE ?";

        List<Aluguel> lista = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, "%" + parametro + "%");
            stmt.setString(2, "%" + parametro + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAluguelCompleto(rs, con));
                }
            }
        }
        return lista;
    }

    @Override
    protected List<Aluguel> executarListagem(Connection con) throws SQLException {
        // JOIN com cliente para já trazer os dados completos
        String sql =
            "SELECT a.id, a.data_emprestimo, a.data_devolucao, a.valor_total, " +
            "       c.id AS c_id, c.nome AS c_nome, c.cpf AS c_cpf, c.endereco AS c_end " +
            "FROM alugueis a " +
            "JOIN cliente c ON a.cliente_id = c.id";

        List<Aluguel> lista = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearAluguelCompleto(rs, con));
            }
        }
        return lista;
    }

    public List<Aluguel> buscarPorMes(int mes, int ano) {
        String sql =
            "SELECT a.id, a.data_emprestimo, a.data_devolucao, a.valor_total, " +
            "       c.id AS c_id, c.nome AS c_nome, c.cpf AS c_cpf, c.endereco AS c_end " +
            "FROM alugueis a " +
            "JOIN cliente c ON a.cliente_id = c.id " +
            "WHERE MONTH(a.data_emprestimo)=? AND YEAR(a.data_emprestimo)=?";

        List<Aluguel> lista = new ArrayList<>();
        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, mes);
            stmt.setInt(2, ano);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAluguelCompleto(rs, con));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na busca por mês: " + e.getMessage());
        } finally {
            ConnectionFactory.getInstance().closeConnection();
        }
        return lista;
    }

    // Monta o Aluguel com Cliente e Produtos já hidratados
    private Aluguel mapearAluguelCompleto(ResultSet rs, Connection con) throws SQLException {
        Aluguel a = new Aluguel();
        a.setId(rs.getInt("id"));

        // Hidratar Cliente completo
        Cliente c = new Cliente();
        c.setId(rs.getInt("c_id"));
        c.setNome(rs.getString("c_nome"));
        c.setCpf(rs.getString("c_cpf"));
        c.setEndereco(rs.getString("c_end"));
        a.setCliente(c);

        a.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());

        Date dataDev = rs.getDate("data_devolucao");
        if (dataDev != null) {
            a.setDataDevolucao(dataDev.toLocalDate());
        }

        a.setValorTotal(rs.getFloat("valor_total"));

        // Hidratar Produtos (livros e discos) via tabela aluguel_produtos
        carregarProdutosDoAluguel(con, a);

        return a;
    }

    // Carrega os produtos (Livro ou Disco) de um aluguel diretamente no objeto
    private void carregarProdutosDoAluguel(Connection con, Aluguel aluguel) throws SQLException {
        // Tenta carregar livros vinculados ao aluguel
        String sqlLivro =
            "SELECT l.* FROM livro l " +
            "JOIN aluguel_produtos ap ON l.id = ap.produto_id " +
            "WHERE ap.aluguel_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sqlLivro)) {
            stmt.setInt(1, aluguel.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Livro livro = new Livro();
                    livro.setId(rs.getInt("id"));
                    livro.setTitulo(rs.getString("titulo"));
                    livro.setAutor(rs.getString("autor"));
                    livro.setGenero(rs.getString("genero"));
                    livro.setExemplares(rs.getInt("exemplares"));
                    livro.setValorAluguel(rs.getFloat("valor_aluguel"));
                    Date ano = rs.getDate("ano");
                    if (ano != null) livro.setAno(ano.toLocalDate());
                    // Adiciona direto na lista sem remover exemplar (já foi removido no aluguel)
                    aluguel.getProdutos().add(livro);
                }
            }
        }

        // Tenta carregar discos vinculados ao aluguel
        String sqlDisco =
            "SELECT d.* FROM disco d " +
            "JOIN aluguel_produtos ap ON d.id = ap.produto_id " +
            "WHERE ap.aluguel_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sqlDisco)) {
            stmt.setInt(1, aluguel.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Disco disco = new Disco();
                    disco.setId(rs.getInt("id"));
                    disco.setTitulo(rs.getString("titulo"));
                    disco.setBanda(rs.getString("banda"));
                    disco.setEstilo(rs.getString("estilo"));
                    disco.setExemplares(rs.getInt("exemplares"));
                    disco.setValorAluguel(rs.getFloat("valor_aluguel"));
                    Date ano = rs.getDate("ano");
                    if (ano != null) disco.setAno(ano.toLocalDate());
                    aluguel.getProdutos().add(disco);
                }
            }
        }
    }
}
