package br.edu.ufersa.LEVI.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Aluguel {

    // Quantos dias o aluguel dura por padrão, e por quantos dias a
    // renovação automática estende o prazo. Mantido em um único lugar
    // para não espalhar o número "7" pelo código.
    public static final int DIAS_PADRAO_ALUGUEL = 7;

    private int id;
    private Cliente cliente;
    private List<Produto> produtos = new ArrayList<>();
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao; // data planejada, definida na criação do aluguel
    private LocalDate dataDevolucao;          // só recebe valor quando o item é devolvido de fato
    private float valorTotal;
    private String status = "Ativo";
    private boolean renovado = false;         // controla se já renovou uma vez

    // Construtor vazio
    public Aluguel() {
        this.dataEmprestimo = LocalDate.now();
        this.dataPrevistaDevolucao = this.dataEmprestimo.plusDays(DIAS_PADRAO_ALUGUEL);
        this.valorTotal = 0;
    }

    // Construtor com cliente e data
    public Aluguel(Cliente cliente, LocalDate dataEmprestimo) {
        setCliente(cliente);
        setDataEmprestimo(dataEmprestimo);
        this.dataPrevistaDevolucao = this.dataEmprestimo.plusDays(DIAS_PADRAO_ALUGUEL);
    }

    // Construtor com produto já incluso
    public Aluguel(Cliente cliente, Produto produto, LocalDate dataEmprestimo) {
        setCliente(cliente);
        setDataEmprestimo(dataEmprestimo);
        this.dataPrevistaDevolucao = this.dataEmprestimo.plusDays(DIAS_PADRAO_ALUGUEL);
        adicionarProduto(produto);
    }

    // Métodos
    public void adicionarProduto(Produto p) {
        if (p != null && p.verificarDisponibilidade()) {
            produtos.add(p);
            p.removerExemplar(1);
            calcularValorTotal();
        } else {
            throw new RuntimeException("Produto indisponível ou inválido!");
        }
    }

    public float calcularValorTotal() {
        float soma = 0;
        for (Produto p : produtos) {
            soma += p.getValorAluguel();
        }
        this.valorTotal = soma;
        return this.valorTotal;
    }

    public void finalizarAluguel(LocalDate dataDevolucao) {
        setDataDevolucao(dataDevolucao);
        this.status = "Finalizado";
        // devolve os exemplares
        for (Produto p : produtos) {
            p.adicionarExemplar(1);
        }
        calcularValorTotal();
    }

    // Verifica se este aluguel se qualifica para renovação automática:
    // ainda está ativo, não foi devolvido, ainda não foi renovado antes,
    // e a data prevista de devolução está a 1 ou 2 dias de vencer.
    // Se renovar, estende a data prevista em mais 7 dias e marca como renovado.
    // Retorna true se a renovação foi aplicada (para o chamador saber se
    // precisa salvar a mudança no banco).
    public boolean verificarERenovarSeNecessario(LocalDate hoje) {
        if (!"Ativo".equals(status)) return false;
        if (dataDevolucao != null) return false;
        if (renovado) return false;
        if (dataPrevistaDevolucao == null) return false;

        long diasParaVencer = java.time.temporal.ChronoUnit.DAYS.between(hoje, dataPrevistaDevolucao);
        boolean prestesAVencer = diasParaVencer >= 0 && diasParaVencer <= 2;

        if (prestesAVencer) {
            this.dataPrevistaDevolucao = this.dataPrevistaDevolucao.plusDays(DIAS_PADRAO_ALUGUEL);
            this.renovado = true;
            return true;
        }
        return false;
    }

    public LocalDate getDataAluguel() {
        return this.dataEmprestimo;
    }

    // Getters
    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public List<Produto> getProdutos() { return produtos; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public LocalDate getDataPrevistaDevolucao() { return dataPrevistaDevolucao; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public float getValorTotal() { return valorTotal; }
    public String getStatus() { return status; }
    public boolean isRenovado() { return renovado; }

    // Setters com validação
    public void setId(int id) { this.id = id; }

    public void setCliente(Cliente cliente) {
        if (cliente != null)
            this.cliente = cliente;
        else
            throw new IllegalArgumentException("Cliente não pode ser nulo!");
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        if (dataEmprestimo != null)
            this.dataEmprestimo = dataEmprestimo;
        else
            this.dataEmprestimo = LocalDate.now();
    }

    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        if (dataDevolucao != null && !dataDevolucao.isBefore(dataEmprestimo))
            this.dataDevolucao = dataDevolucao;
        else
            throw new IllegalArgumentException("Data de devolução inválida!");
    }

    public void setValorTotal(float valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setStatus(String status) { this.status = status; }

    public void setRenovado(boolean renovado) { this.renovado = renovado; }
}
