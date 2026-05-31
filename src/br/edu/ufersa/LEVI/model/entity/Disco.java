package br.edu.ufersa.LEVI.model.entity;
import java.time.LocalDate;

public class Disco extends Produto {
    private String banda;
    private String estilo;

    public Disco() {
        super();
        this.banda = "Sem banda";
        this.estilo = "Sem estilo";
    }

    public Disco(String titulo, String banda, String estilo,
                 int exemplares, float valorAluguel, LocalDate ano) {
        super(titulo, exemplares, valorAluguel, ano);
        setBanda(banda);
        setEstilo(estilo);
    }

    @Override
    public String getDescricao() {
        return "Disco: " + getTitulo() + " | Banda: " + banda + " | Estilo: " + estilo;
    }

    @Override
    public boolean contemTermo(String termo) {
        return getTitulo().toLowerCase().contains(termo.toLowerCase())
                || banda.toLowerCase().contains(termo.toLowerCase())
                || estilo.toLowerCase().contains(termo.toLowerCase());
    }

    // Getters e Setters específicos de Disco
    public String getBanda() { return banda; }
    public String getEstilo() { return estilo; }

    public void setBanda(String banda) {
        if (banda != null && !banda.isEmpty()) this.banda = banda;
        else this.banda = "Sem banda";
    }

    public void setEstilo(String estilo) {
        if (estilo != null && !estilo.isEmpty()) this.estilo = estilo;
        else this.estilo = "Sem estilo";
    }
}