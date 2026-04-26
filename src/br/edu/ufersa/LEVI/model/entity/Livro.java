package br.edu.ufersa.LEVI.model.entity;

import java.time.LocalDate;

public class Livro {

    private String titulo;
    private String genero;
    private LocalDate ano;
    private String autor;
    private int exemplares;
    private int paginas;
    private float valorAluguel;

    public Livro() {
        this.titulo = "Sem título";
        this.genero = "Sem gênero";
        this.ano = LocalDate.now();
        this.autor = "Sem autor";
        this.paginas = 1;
        this.exemplares = 0;
        this.valorAluguel = 0;
    }


    public Livro(String titulo, String genero, LocalDate ano, String autor, int paginas, int exemplares, float valorAluguel) {
        setTitulo(titulo);
        setGenero(genero);
        setAno(ano);
        setAutor(autor);
        setPaginas(paginas);
        setExemplares(exemplares);
        setValorAluguel(valorAluguel);
    }


    // Métodos
    public void atualizarValorAluguel(float novoValor) {
        setValorAluguel(novoValor);
    }

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


    public float getValorAluguel() {
        return valorAluguel;
    }

    public int getExemplares() {
        return exemplares;
    }

    public int getPaginas() {
        return paginas;
    }

    public LocalDate getAno() {
        return ano;
    }

    public String getAutor() {
        return autor;
    }

    public String getTitulo() {
        return titulo;
    }

    // Setters com validação
    public void setTitulo(String titulo) {
        if (titulo != null && !titulo.isEmpty()) {
            this.titulo = titulo;
        } else {
            this.titulo = "Sem título";
        }
    }

    public void setGenero(String genero) {
        if (genero != null && !genero.isEmpty()) {
            this.genero = genero;
        } else {
            this.genero = "Sem gênero";
        }
    }

    public void setAno(LocalDate ano) {
        if (ano != null && !ano.isAfter(LocalDate.now())) {
            this.ano = ano;
        } else {
            this.ano = LocalDate.now();
        }
    }

    public void setAutor(String autor) {
        if (autor != null && !autor.isEmpty()) {
            this.autor = autor;
        } else {
            this.autor = "Sem autor";
        }
    }

    public void setPaginas(int paginas) {
        if (paginas > 0) {
            this.paginas = paginas;
        } else {
            this.paginas = 1;
        }
    }

    public void setExemplares(int exemplares) {
        if (exemplares >= 0) {
            this.exemplares = exemplares;
        } else {
            this.exemplares = 0;
        }
    }

    public void setValorAluguel(float valorAluguel) {
        if (valorAluguel >= 0) {
            this.valorAluguel = valorAluguel;
        } else {
            this.valorAluguel = 0;
        }
    }
}
