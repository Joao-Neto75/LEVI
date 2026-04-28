package br.edu.ufersa.LEVI.model.entity;

import java.util.ArrayList; // Faltava esse import
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Aluguel {
    private Cliente cliente;
    private List<Livro> livrosAlugados = new ArrayList<>();
    private List<Disco> discosAlugados = new ArrayList<>();
    private String dataEmprestimo;
    private String dataDevolucao;
    private float valorTotal;

    public Aluguel(Cliente cliente, Livro livro, String dataEmprestimo) {
        this.cliente = cliente;
        this.livrosAlugados.add(livro);
        this.dataEmprestimo = dataEmprestimo;
        this.calcularValorTotal();
    }
    public Aluguel(Cliente cliente, Disco disco, String dataEmprestimo) {
        this.cliente = cliente;
        this.discosAlugados.add(disco);
        this.dataEmprestimo = dataEmprestimo;
        this.calcularValorTotal();
    }

    public float calcularValorTotal() {
        float soma = 0;
        for (Livro l : livrosAlugados) {
            soma += l.getValorAluguel();
        }
        for (Disco d : discosAlugados) {
            soma += d.getValorAluguel();
        }
        this.valorTotal = soma;
        return this.valorTotal;
    }

    public void finalizarAluguel(String data) {
        this.dataDevolucao = data;
        this.calcularValorTotal();
    }

    public Cliente getCliente(){
        return this.cliente;
    }

    public List<Livro> getLivrosAlugados(){
        return this.livrosAlugados;
    }

    public List<Disco> getDiscosAlugados(){
        return this.discosAlugados;
    }

    public String getDataEmprestimo(){
        return this.dataEmprestimo;
    }

    public String getDataDevolucao(){
        return this.dataDevolucao;
    }

    public void setDataDevolucao(String dataDevolucao){
        this.dataDevolucao = dataDevolucao;
    }

    public float getValorTotal() {
        return this.valorTotal;
    }

    public void addLivro(Livro livro) {
        this.livrosAlugados.add(livro);
        this.calcularValorTotal();
    }

    public void addDisco(Disco disco) {
        this.discosAlugados.add(disco);
        this.calcularValorTotal();
    }

    public LocalDate getDataAluguel() {
    return LocalDate.parse(this.dataEmprestimo);
}
}
