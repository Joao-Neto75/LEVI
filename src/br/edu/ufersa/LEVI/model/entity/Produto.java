package br.edu.ufersa.LEVI.model.entity;

import java.time.LocalDate;

public abstract class Produto implements Pesquisavel{
    private int id;
    private String titulo;
    private int exemplares;
    private float valorAluguel;
    private LocalDate ano;

    // Construtor vazio
    public Produto() {
        this.titulo = "Sem título";
        this.exemplares = 0;
        this.valorAluguel = 0;
        this.ano = LocalDate.now();
    }

    // Construtor completo
    public Produto(String titulo, int exemplares, float valorAluguel, LocalDate ano) {
        setTitulo(titulo);
        setExemplares(exemplares);
        setValorAluguel(valorAluguel);
        setAno(ano);
    }


    public abstract String getDescricao();
    public abstract boolean contemTermo(String termo);

    // Métodos comuns
    public void adicionarExemplar(int quantidade) {
        if (quantidade <= 0)
            throw new IllegalArgumentException("Quantidade deve ser maior que zero!");
        this.exemplares += quantidade;
    }

    public void removerExemplar(int quantidade) {
        if (quantidade <= 0)
            throw new IllegalArgumentException("Quantidade deve ser maior que zero!");
        if (quantidade > this.exemplares)
            throw new IllegalArgumentException("Quantidade maior que o estoque disponível!");
        this.exemplares -= quantidade;
    }

    public boolean verificarDisponibilidade() {
        return this.exemplares > 0;
    }

    // Getters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public int getExemplares() { return exemplares; }
    public float getValorAluguel() { return valorAluguel; }
    public LocalDate getAno() { return ano; }

    // Setters com validação
    public void setId(int id) { this.id = id; }

    public void setTitulo(String titulo) {
        if (titulo != null && !titulo.isEmpty())
            this.titulo = titulo;
        else
            this.titulo = "Sem título";
    }

    public void setExemplares(int exemplares) {
        if (exemplares >= 0)
            this.exemplares = exemplares;
        else
            this.exemplares = 0;
    }

    public void setValorAluguel(float valorAluguel) {
        if (valorAluguel >= 0)
            this.valorAluguel = valorAluguel;
        else
            this.valorAluguel = 0;
    }

    public void setAno(LocalDate ano) {
        if (ano != null && !ano.isAfter(LocalDate.now()))
            this.ano = ano;
        else
            this.ano = LocalDate.now();
    }
}