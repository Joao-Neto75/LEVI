package br.edu.ufersa.LEVI.model.entity;
import java.time.LocalDate;


public class Livro extends Produto {
    private String genero;
    private String autor;
    private int paginas;

    public Livro() {
        super();
        this.genero = "Sem gênero";
        this.autor = "Sem autor";
        this.paginas = 1;
    }

    public Livro(String titulo, String genero, LocalDate ano, String autor,
                 int paginas, int exemplares, float valorAluguel) {
        super(titulo, exemplares, valorAluguel, ano);
        setGenero(genero);
        setAutor(autor);
        setPaginas(paginas);
    }

    @Override
    public String getDescricao() {
        return "Livro: " + getTitulo() + " | Autor: " + autor + " | Gênero: " + genero;
    }

    @Override
    public boolean contemTermo(String termo) {
        return getTitulo().toLowerCase().contains(termo.toLowerCase())
                || autor.toLowerCase().contains(termo.toLowerCase())
                || genero.toLowerCase().contains(termo.toLowerCase());
    }

    // Getters e Setters específicos de Livro
    public String getGenero() {
        return genero;
    }

    public String getAutor() {
        return autor;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setGenero(String genero) {
        if (genero != null && !genero.isEmpty()) this.genero = genero;
        else this.genero = "Sem gênero";
    }

    public void setAutor(String autor) {
        if (autor != null && !autor.isEmpty()) this.autor = autor;
        else this.autor = "Sem autor";
    }

    public void setPaginas(int paginas) {
        if (paginas > 0) this.paginas = paginas;
        else this.paginas = 1;
    }
}