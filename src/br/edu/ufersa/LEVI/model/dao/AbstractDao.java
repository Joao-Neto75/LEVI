package br.edu.ufersa.LEVI.model.dao;

import br.edu.ufersa.LEVI.connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

// Implementa o padrão Template Method: define o ROTEIRO fixo de como toda
// operação no banco deve acontecer (abrir conexão -> executar a query
// específica -> tratar erro -> fechar conexão), e deixa só a parte que muda
// de DAO para DAO (o "executarX") para as subclasses escreverem.
//
// Antes, cada DAO (ClienteDao, LivroDao, DiscoDao...) repetia o mesmo
// try/catch/finally inteiro, só troncando o SQL e os parâmetros no meio.
// Agora esse "molde" fica escrito uma única vez aqui.
public abstract class AbstractDao<E> implements BaseDao<E> {

    // ===================== MÉTODOS "TEMPLATE" (o roteiro fixo) =====================
    // Cada um deles segue sempre os mesmos 4 passos. As subclasses não podem
    // mudar essa ordem — só implementam o que querem executar em cada passo,
    // através dos métodos abstratos abaixo (executarInsercao, executarAlteracao...).

    @Override
    public final E inserir(E entity) {
        Connection con = ConnectionFactory.getInstance().getConnection();
        try {
            return executarInsercao(con, entity);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na inserção: " + e.getMessage());
        } finally {
            ConnectionFactory.getInstance().closeConnection();
        }
    }

    @Override
    public final E alterar(E entity) {
        Connection con = ConnectionFactory.getInstance().getConnection();
        try {
            return executarAlteracao(con, entity);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na atualização: " + e.getMessage());
        } finally {
            ConnectionFactory.getInstance().closeConnection();
        }
    }

    @Override
    public final E deletar(E entity) {
        Connection con = ConnectionFactory.getInstance().getConnection();
        try {
            return executarExclusao(con, entity);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na exclusão: " + e.getMessage());
        } finally {
            ConnectionFactory.getInstance().closeConnection();
        }
    }

    @Override
    public final List<E> buscar(String parametro) {
        Connection con = ConnectionFactory.getInstance().getConnection();
        try {
            return executarBusca(con, parametro);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na busca: " + e.getMessage());
        } finally {
            ConnectionFactory.getInstance().closeConnection();
        }
    }

    @Override
    public final List<E> listar() {
        Connection con = ConnectionFactory.getInstance().getConnection();
        try {
            return executarListagem(con);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na listagem: " + e.getMessage());
        } finally {
            ConnectionFactory.getInstance().closeConnection();
        }
    }

    // ===================== "GANCHOS" (passos que cada DAO concreto define) =====================
    // É aqui que ClienteDao, LivroDao, DiscoDao etc. escrevem o SQL e os
    // parâmetros específicos de cada um. O AbstractDao já cuidou de abrir a
    // conexão, tratar erro e fechar — essas subclasses só executam a query.

    protected abstract E executarInsercao(Connection con, E entity) throws SQLException;

    protected abstract E executarAlteracao(Connection con, E entity) throws SQLException;

    protected abstract E executarExclusao(Connection con, E entity) throws SQLException;

    protected abstract List<E> executarBusca(Connection con, String parametro) throws SQLException;

    protected abstract List<E> executarListagem(Connection con) throws SQLException;
}
