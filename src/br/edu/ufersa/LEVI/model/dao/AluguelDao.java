package br.edu.ufersa.LEVI.model.dao;

import br.edu.ufersa.LEVI.connection.ConnectionFactory;
import br.edu.ufersa.LEVI.model.entity.Aluguel;
import br.edu.ufersa.LEVI.model.entity.Cliente;
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

        // insere os produtos do aluguel na tabela aluguel_produtos
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
        // remove os produtos do aluguel primeiro (chave estrangeira)
        try (PreparedStatement stmt1 = con.prepareStatement("DELETE FROM aluguel_produtos WHERE aluguel_id=?")) {
            stmt1.setInt(1, entity.getId());
            stmt1.execute();
        }

        // remove o aluguel
        try (PreparedStatement stmt2 = con.prepareStatement("DELETE FROM alugueis WHERE id=?")) {
            stmt2.setInt(1, entity.getId());
            stmt2.execute();
        }

        return entity;
    }

    @Override
    protected List<Aluguel> executarBusca(Connection con, String parametro) throws SQLException {
        // busca por CPF ou nome do cliente
        String sql = "SELECT a.* FROM alugueis a " +
                "JOIN clientes c ON a.cliente_id = c.id " +
                "WHERE c.cpf LIKE ? OR c.nome LIKE ?";
        List<Aluguel> lista = new ArrayList<>();

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, "%" + parametro + "%");
            stmt.setString(2, "%" + parametro + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAluguel(rs));
                }
            }
        }
        return lista;
    }

    @Override
    protected List<Aluguel> executarListagem(Connection con) throws SQLException {
        String sql = "SELECT * FROM alugueis";
        List<Aluguel> lista = new ArrayList<>();

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAluguel(rs));
            }
        }
        return lista;
    }

    // Método específico de AluguelDao, usado para o relatório por mês. Não faz
    // parte do contrato BaseDao, então abre e fecha a conexão manualmente,
    // como já fazia antes da migração.
    public List<Aluguel> buscarPorMes(int mes, int ano) {
        String sql = "SELECT * FROM alugueis WHERE MONTH(data_emprestimo)=? AND YEAR(data_emprestimo)=?";
        List<Aluguel> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, mes);
            stmt.setInt(2, ano);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAluguel(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na busca por mês: " + e.getMessage());
        } finally {
            ConnectionFactory.getInstance().closeConnection();
        }
        return lista;
    }

    private Aluguel mapearAluguel(ResultSet rs) throws SQLException {
        Aluguel a = new Aluguel();
        a.setId(rs.getInt("id"));

        Cliente c = new Cliente();
        c.setId(rs.getInt("cliente_id"));
        a.setCliente(c);

        a.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());

        Date dataDev = rs.getDate("data_devolucao");
        if (dataDev != null) {
            a.setDataDevolucao(dataDev.toLocalDate());
        }

        a.setValorTotal(rs.getFloat("valor_total"));
        return a;
    }
}