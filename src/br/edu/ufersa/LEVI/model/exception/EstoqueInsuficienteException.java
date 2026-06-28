package br.edu.ufersa.LEVI.model.exception;

/**
 * Lançada quando se tenta remover do estoque uma quantidade
 * maior do que a disponível para determinado produto.
 * É uma RuntimeException (unchecked) pois indica um estado
 * inválido que não deveria ocorrer se as regras anteriores
 * (verificarDisponibilidade) forem respeitadas.
 */
public class EstoqueInsuficienteException extends RuntimeException {

    private final String tituloProduto;
    private final int disponiveis;
    private final int solicitados;

    public EstoqueInsuficienteException(String tituloProduto, int disponiveis, int solicitados) {
        super("Estoque insuficiente para \"" + tituloProduto + "\": " +
              disponiveis + " disponível(is), solicitado(s): " + solicitados + ".");
        this.tituloProduto = tituloProduto;
        this.disponiveis   = disponiveis;
        this.solicitados   = solicitados;
    }

    public String getTituloProduto() { return tituloProduto; }
    public int getDisponiveis()      { return disponiveis; }
    public int getSolicitados()      { return solicitados; }
}
