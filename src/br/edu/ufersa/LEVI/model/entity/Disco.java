package br.edu.ufersa.LEVI.model.entity;

public class Disco {
    private String titulo;
    private String banda;
    private String estilo;
    private int exemplares;
    private float valorAluguel;

    // Construtor vazio
    public Disco() {
        this.titulo = "Sem título";
        this.banda = "Sem banda";
        this.estilo = "Sem estilo";
        this.exemplares = 0;
        this.valorAluguel = 0;
    }

    // Construtor completo
    public Disco(String titulo, String banda, String estilo, int exemplares, float valorAluguel) {
        setTitulo(titulo);
        setBanda(banda);
        setEstilo(estilo);
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

    // Getters
    public String getTitulo() { return titulo; }
    public String getBanda() { return banda; }
    public String getEstilo() { return estilo; }
    public int getExemplares() { return exemplares; }
    public float getValorAluguel() { return valorAluguel; }

    // Setters com validação
    public void setTitulo(String titulo) {
        if (titulo != null && !titulo.isEmpty()) {
            this.titulo = titulo;
        } else {
            this.titulo = "Sem título";
        }
    }

    public void setBanda(String banda) {
        if (banda != null && !banda.isEmpty()) {
            this.banda = banda;
        } else {
            this.banda = "Sem banda";
        }
    }

    public void setEstilo(String estilo) {
        if (estilo != null && !estilo.isEmpty()) {
            this.estilo = estilo;
        } else {
            this.estilo = "Sem estilo";
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