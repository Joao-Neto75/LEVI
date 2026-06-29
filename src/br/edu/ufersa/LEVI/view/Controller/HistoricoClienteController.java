package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.model.entity.Aluguel;
import br.edu.ufersa.LEVI.model.entity.Cliente;
import br.edu.ufersa.LEVI.model.entity.Disco;
import br.edu.ufersa.LEVI.model.entity.Livro;
import br.edu.ufersa.LEVI.model.entity.Produto;
import br.edu.ufersa.LEVI.model.service.LocadoraFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoricoClienteController {

    @FXML private Label labelNomeCliente;
    @FXML private Label labelCpf;
    @FXML private Label labelEndereco;
    @FXML private Label labelStatus;
    @FXML private Label labelTotalItens;

    @FXML private TableView<LinhaHistorico> tabelaHistorico;
    @FXML private TableColumn<LinhaHistorico, String> colTipo;
    @FXML private TableColumn<LinhaHistorico, String> colProduto;
    @FXML private TableColumn<LinhaHistorico, String> colEmprestimo;
    @FXML private TableColumn<LinhaHistorico, String> colDevolucao;
    @FXML private TableColumn<LinhaHistorico, String> colStatus;
    @FXML private TableColumn<LinhaHistorico, String> colValor;

    @FXML private Label labelTotalAlugueis;
    @FXML private Label labelMulta;
    @FXML private Label labelTotalValor;

    private final LocadoraFacade facade = new LocadoraFacade();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final float MULTA_POR_DIA = 5.0f; // R$5,00 por dia de atraso

    public static class LinhaHistorico {
        final String tipo, produto, emprestimo, devolucao, status, valor;
        final float multa;

        public LinhaHistorico(String tipo, String produto, String emprestimo,
                              String devolucao, String status, float valor, float multa) {
            this.tipo       = tipo;
            this.produto    = produto;
            this.emprestimo = emprestimo;
            this.devolucao  = devolucao;
            this.status     = status;
            this.valor      = String.format("R$ %.2f", valor);
            this.multa      = multa;
        }
    }

    public void carregarCliente(Cliente cliente) {
        labelNomeCliente.setText(cliente.getNome());
        labelCpf.setText("CPF: " + cliente.getCpf());
        labelEndereco.setText("Endereço: " + cliente.getEndereco());

        List<Aluguel> alugueis = facade.buscarAlugueisPorCliente(cliente);

        boolean temAtraso = alugueis.stream().anyMatch(a ->
                a.getDataDevolucao() != null
                        && a.getDataDevolucao().isBefore(LocalDate.now())
                        && !"Finalizado".equals(a.getStatus())
        );
        labelStatus.setText("Status: " + (temAtraso ? "Atrasado" : "Regular"));
        labelStatus.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: "
                + (temAtraso ? "#cc0000" : "#228B22") + ";");

        configurarTabela();
        var linhas = FXCollections.<LinhaHistorico>observableArrayList();

        int totalItens  = 0;
        float totalValor = 0f;
        float totalMulta = 0f;

        for (Aluguel aluguel : alugueis) {

            // Calcula multa UMA VEZ por aluguel (não por produto)
            float multaDoAluguel = 0f;
            String statusAluguel;

            if ("Finalizado".equals(aluguel.getStatus())) {
                statusAluguel = "Devolvido";
            } else if (aluguel.getDataDevolucao() != null
                    && aluguel.getDataDevolucao().isBefore(LocalDate.now())) {
                long diasAtraso = LocalDate.now().toEpochDay()
                        - aluguel.getDataDevolucao().toEpochDay();
                multaDoAluguel = diasAtraso * MULTA_POR_DIA;
                statusAluguel  = "Atrasado (" + diasAtraso + "d)";
            } else {
                statusAluguel = "Ativo";
            }

            // Acumula multa apenas uma vez por aluguel
            totalMulta += multaDoAluguel;

            String dataEmp = aluguel.getDataEmprestimo() != null
                    ? aluguel.getDataEmprestimo().format(FMT) : "—";
            String dataDev = aluguel.getDataDevolucao() != null
                    ? aluguel.getDataDevolucao().format(FMT) : "—";

            // Cria uma linha por produto, mas multa aparece só na primeira linha do aluguel
            boolean primeiroItem = true;
            for (Produto produto : aluguel.getProdutos()) {
                totalItens++;

                String tipo     = (produto instanceof Livro) ? "Livro" : "Disco";
                String nomeProd = produto.getTitulo();
                if (produto instanceof Livro livro) {
                    nomeProd += " — " + livro.getAutor();
                } else if (produto instanceof Disco disco) {
                    nomeProd += " — " + disco.getBanda();
                }

                float valorProduto = produto.getValorAluguel();
                totalValor += valorProduto;

                // Multa só aparece na primeira linha do aluguel; demais ficam com 0
                linhas.add(new LinhaHistorico(
                        tipo, nomeProd, dataEmp, dataDev,
                        statusAluguel, valorProduto,
                        primeiroItem ? multaDoAluguel : 0f
                ));
                primeiroItem = false;
            }
        }

        tabelaHistorico.setItems(linhas);

        // Rodapé
        labelTotalItens.setText("Itens: " + totalItens);
        labelTotalAlugueis.setText("Total de alugueis: " + alugueis.size());
        labelMulta.setText(String.format("Multa por atraso: R$ %.2f", totalMulta));
        // Valor total = valor dos produtos + multa
        labelTotalValor.setText(String.format("Valor total: R$ %.2f", totalValor + totalMulta));

        labelMulta.setVisible(totalMulta > 0);
        labelMulta.setManaged(totalMulta > 0);
    }

    private void configurarTabela() {
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tipo));
        colProduto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().produto));
        colEmprestimo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().emprestimo));
        colDevolucao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().devolucao));
        colValor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().valor));

        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setText(null); setStyle(""); return; }
                setText(item);
                if (item.startsWith("Atrasado")) {
                    setStyle("-fx-text-fill: #cc0000; -fx-font-weight: bold;");
                } else if ("Devolvido".equals(item)) {
                    setStyle("-fx-text-fill: #555;");
                } else {
                    setStyle("-fx-text-fill: #228B22; -fx-font-weight: bold;");
                }
            }
        });

        tabelaHistorico.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(LinhaHistorico item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                setStyle(item.status.startsWith("Atrasado")
                        ? "-fx-background-color: #fff0f0;" : "");
            }
        });
    }

    @FXML
    public void handleFechar() {
        Stage stage = (Stage) tabelaHistorico.getScene().getWindow();
        stage.close();
    }
}
