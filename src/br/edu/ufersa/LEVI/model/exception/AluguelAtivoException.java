package br.edu.ufersa.LEVI.model.exception;

/**
 * Lançada quando se tenta excluir um cliente que ainda possui
 * alugueis ativos (itens que não foram devolvidos).
 * É uma Exception checked para forçar o chamador a tratar
 * explicitamente esse caso antes de prosseguir com a exclusão.
 */
public class AluguelAtivoException extends Exception {

    private final String nomeCliente;
    private final int quantidadeItens;

    public AluguelAtivoException(String nomeCliente, int quantidadeItens) {
        super("Não é possível excluir o cliente \"" + nomeCliente +
              "\" pois ele possui " + quantidadeItens + " item(ns) em aberto.");
        this.nomeCliente = nomeCliente;
        this.quantidadeItens = quantidadeItens;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public int getQuantidadeItens() {
        return quantidadeItens;
    }
}
