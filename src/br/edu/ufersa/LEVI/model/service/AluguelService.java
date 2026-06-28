package br.edu.ufersa.LEVI.model.service;

import br.edu.ufersa.LEVI.model.dao.AluguelDao;
import br.edu.ufersa.LEVI.model.entity.Aluguel;
import br.edu.ufersa.LEVI.model.entity.Cliente;
import br.edu.ufersa.LEVI.model.entity.Produto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AluguelService {
    private AluguelDao dao = new AluguelDao();

    public Aluguel registrar(Aluguel entity) {
        // Regra: cliente não pode ser nulo
        if (entity.getCliente() == null) {
            throw new RuntimeException("Aluguel precisa ter um cliente!");
        }
        // Regra: precisa ter pelo menos um produto
        if (entity.getProdutos().isEmpty()) {
            throw new RuntimeException("Aluguel precisa ter pelo menos um produto!");
        }
        // Regra: verifica disponibilidade de cada produto
        for (Produto p : entity.getProdutos()) {
            if (!p.verificarDisponibilidade()) {
                throw new RuntimeException("Produto indisponível: " + p.getTitulo());
            }
        }
        return dao.inserir(entity);
    }

    public Aluguel finalizar(Aluguel entity, LocalDate dataDevolucao) {
        // Regra: data de devolução não pode ser antes da data de empréstimo
        if (dataDevolucao.isBefore(entity.getDataEmprestimo())) {
            throw new RuntimeException("Data de devolução inválida!");
        }
        entity.finalizarAluguel(dataDevolucao);
        return dao.alterar(entity);
    }

    public Aluguel deletar(Aluguel entity) {
        return dao.deletar(entity);
    }

    public List<Aluguel> buscar(String parametro) {
        return dao.buscar(parametro);
    }

    public List<Aluguel> listar() {
        return dao.listar();
    }

    public List<Aluguel> buscarPorMes(int mes, int ano) {
        return dao.buscarPorMes(mes, ano);
    }

    public float calcularFaturamentoMes(int mes, int ano) {
        List<Aluguel> alugueis = dao.buscarPorMes(mes, ano);
        float total = 0;
        for (Aluguel a : alugueis) {
            total += a.getValorTotal();
        }
        return total;
    }

    public List<Aluguel> buscarPorCliente(Cliente c) {
        return dao.buscar(c.getCpf());
    }

    // Verifica todos os aluguéis ativos e renova automaticamente quem está
    // a 1-2 dias de vencer (regra implementada em Aluguel.verificarERenovarSeNecessario).
    // Persiste no banco cada renovação aplicada e devolve a lista dos
    // aluguéis que de fato foram renovados nesta chamada, para a tela
    // de dashboard poder exibi-los no card "Renovações automáticas".
    public List<Aluguel> processarRenovacoesAutomaticas() {
        List<Aluguel> renovados = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        List<Aluguel> ativos = dao.buscarAtivos();
        for (Aluguel aluguel : ativos) {
            boolean foiRenovado = aluguel.verificarERenovarSeNecessario(hoje);
            if (foiRenovado) {
                dao.salvarRenovacao(aluguel);
                renovados.add(aluguel);
            }
        }
        return renovados;
    }
}
