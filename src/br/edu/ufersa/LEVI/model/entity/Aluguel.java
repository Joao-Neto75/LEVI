package br.edu.ufersa.LEVI.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Aluguel {
    private int id;
    private Cliente cliente;
    private List<Produto> produtos = new ArrayList<>();
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;
    private float valorTotal;
    private String status = "Ativo";

    // Construtor vazio
    public Aluguel() {
        this.dataEmprestimo = LocalDate.now();
        this.valorTotal = 0;
    }

    // Construtor com cliente e data
    public Aluguel(Cliente cliente, LocalDate dataEmprestimo) {
        setCliente(cliente);
        setDataEmprestimo(dataEmprestimo);
    }

    // Construtor com produto já incluso
    public Aluguel(Cliente cliente, Produto produto, LocalDate dataEmprestimo) {
        setCliente(cliente);
        setDataEmprestimo(dataEmprestimo);
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

    public LocalDate getDataAluguel() {
        return this.dataEmprestimo;
    }

    // Getters
    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public List<Produto> getProdutos() { return produtos; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public float getValorTotal() { return valorTotal; }
    public String getStatus() { return status; }

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
}