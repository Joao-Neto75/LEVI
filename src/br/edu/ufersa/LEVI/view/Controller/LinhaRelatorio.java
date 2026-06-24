package br.edu.ufersa.LEVI.view.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Representa uma linha da tabela de relatório: um único produto (livro ou disco)
// dentro de um aluguel. Um Aluguel pode gerar várias linhas, uma para cada produto.
public class LinhaRelatorio {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yy");

    private final String cliente;
    private final String item;
    private final LocalDate dataInicio;
    private final LocalDate dataFim;
    private final String status;
    private final float valor;

    public LinhaRelatorio(String cliente, String item, LocalDate dataInicio, LocalDate dataFim,
                           String status, float valor) {
        this.cliente = cliente;
        this.item = item;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.valor = valor;
    }

    public String getCliente() {
        return cliente;
    }

    public String getItem() {
        return item;
    }

    public String getDataInicioFormatada() {
        return dataInicio != null ? dataInicio.format(FORMATO_DATA) : "-";
    }

    public String getDataFimFormatada() {
        return dataFim != null ? dataFim.format(FORMATO_DATA) : "-";
    }

    // Antes este método calculava o status com um prazo fixo de 7 dias.
    // Agora a entidade Aluguel já guarda o status real ("Ativo"/"Finalizado"),
    // então a LinhaRelatorio só repassa o valor recebido no construtor.
    public String getStatus() {
        return status;
    }

    public float getValor() {
        return valor;
    }

    public String getValorFormatado() {
        return String.format("R$ %.2f", valor);
    }
}
