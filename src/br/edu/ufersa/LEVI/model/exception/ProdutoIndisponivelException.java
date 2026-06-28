package br.edu.ufersa.LEVI.model.exception;

/**
 * Lançada quando se tenta alugar um produto que não possui
 * exemplares disponíveis em estoque.
 * É uma RuntimeException (unchecked) porque representa uma
 * violação de regra de negócio que deve ser tratada pela camada
 * de serviço / controller, sem obrigar cada chamador a declarar
 * throws.
 */
public class ProdutoIndisponivelException extends RuntimeException {

    private final String tituloProduto;

    public ProdutoIndisponivelException(String tituloProduto) {
        super("O produto \"" + tituloProduto + "\" não possui exemplares disponíveis para aluguel.");
        this.tituloProduto = tituloProduto;
    }

    public String getTituloProduto() {
        return tituloProduto;
    }
}
