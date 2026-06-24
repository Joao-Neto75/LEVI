package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.App;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AlugueisController {

    // Formulário novo aluguel
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Livro> comboLivro;
    @FXML private ComboBox<Disco> comboDisco;
    @FXML private DatePicker dataEmprestimo;
    @FXML private DatePicker dataDevolucao;
    @FXML private Label labelValorTotal;
    @FXML private Label labelErroForm;
    @FXML private Label labelErro;

    // Tabela
    @FXML private TableView<LinhaAluguel> tabelaAlugueis;
    @FXML private TableColumn<LinhaAluguel, String> colCliente;
    @FXML private TableColumn<LinhaAluguel, String> colTipo;
    @FXML private TableColumn<LinhaAluguel, String> colTitulo;
    @FXML private TableColumn<LinhaAluguel, String> colDatas;
    @FXML private TableColumn<LinhaAluguel, String> colStatus;

    private final LocadoraFacade facade = new LocadoraFacade();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yy");

    @FXML
    public void initialize() {
        configurarCombos();
        configurarTabela();
        carregarAlugueis();
        configurarListeners();
    }

    private void configurarCombos() {
        comboCliente.setItems(FXCollections.observableArrayList(facade.listarClientes()));
        comboCliente.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(c == null || empty ? "Selecionar..." : c.getNome());
            }
        });
        comboCliente.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(c == null || empty ? "" : c.getNome() + " — " + c.getCpf());
            }
        });

        comboLivro.setItems(FXCollections.observableArrayList(facade.listarLivros()));
        comboLivro.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Livro l, boolean empty) {
                super.updateItem(l, empty);
                setText(l == null || empty ? "Selecionar..." : l.getTitulo());
            }
        });
        comboLivro.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Livro l, boolean empty) {
                super.updateItem(l, empty);
                setText(l == null || empty ? "" : l.getTitulo() + " — R$ " + l.getValorAluguel());
            }
        });

        comboDisco.setItems(FXCollections.observableArrayList(facade.listarDiscos()));
        comboDisco.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Disco d, boolean empty) {
                super.updateItem(d, empty);
                setText(d == null || empty ? "Selecionar..." : d.getTitulo());
            }
        });
        comboDisco.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Disco d, boolean empty) {
                super.updateItem(d, empty);
                setText(d == null || empty ? "" : d.getTitulo() + " — R$ " + d.getValorAluguel());
            }
        });

        dataEmprestimo.setValue(LocalDate.now());
    }

    private void configurarListeners() {
        comboLivro.setOnAction(e -> atualizarValorTotal());
        comboDisco.setOnAction(e -> atualizarValorTotal());
        // Limpar seleção mutuamente exclusiva (um livro OU um disco por vez)
        comboLivro.setOnAction(e -> {
            if (comboLivro.getValue() != null) comboDisco.getSelectionModel().clearSelection();
            atualizarValorTotal();
        });
        comboDisco.setOnAction(e -> {
            if (comboDisco.getValue() != null) comboLivro.getSelectionModel().clearSelection();
            atualizarValorTotal();
        });
    }

    private void atualizarValorTotal() {
        float total = 0;
        if (comboLivro.getValue() != null) total += comboLivro.getValue().getValorAluguel();
        if (comboDisco.getValue() != null) total += comboDisco.getValue().getValorAluguel();
        labelValorTotal.setText(String.format("R$ %.2f", total));
    }

    private void configurarTabela() {
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nomeCliente));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tipo));
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().titulo));
        colDatas.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().datas));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setText(null); setStyle(""); return; }
                setText(item);
                if (item.startsWith("Atrasado")) {
                    setStyle("-fx-text-fill: #cc0000; -fx-font-weight: bold;");
                } else if (item.equals("Finalizado")) {
                    setStyle("-fx-text-fill: #808080; -fx-font-weight: normal;");
                } else {
                    setStyle("-fx-text-fill: #228B22; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void carregarAlugueis() {
        List<Aluguel> alugueis = facade.listarAlugueis();
        java.util.List<LinhaAluguel> linhas = new java.util.ArrayList<>();

        for (Aluguel a : alugueis) {
            String nomeCliente = a.getCliente() != null ? a.getCliente().getNome() : "—";
            String inicio = a.getDataEmprestimo() != null ? a.getDataEmprestimo().format(FMT) : "—";
            String fim = a.getDataDevolucao() != null ? a.getDataDevolucao().format(FMT) : "—";
            String datas = inicio + " - " + fim;

            String status = a.getStatus();
            if (status.equals("Ativo") && a.getDataDevolucao() != null) {
                long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(a.getDataDevolucao(), LocalDate.now());
                if (diasAtraso > 0) status = "Atrasado (" + diasAtraso + " dias)";
            }

            for (Produto p : a.getProdutos()) {
                String tipo = p.getClass().getSimpleName();
                linhas.add(new LinhaAluguel(nomeCliente, tipo, p.getDescricao().split("\\|")[0].trim(), datas, status));
            }
        }

        tabelaAlugueis.setItems(FXCollections.observableArrayList(linhas));
    }

    @FXML
    public void handleNovoAluguel() {
        labelErroForm.setText("");
        try {
            Cliente cliente = comboCliente.getValue();
            Livro livro = comboLivro.getValue();
            Disco disco = comboDisco.getValue();
            LocalDate inicio = dataEmprestimo.getValue();

            if (cliente == null) { labelErroForm.setText("Selecione um cliente."); return; }
            if (livro == null && disco == null) { labelErroForm.setText("Selecione um livro ou disco."); return; }
            if (inicio == null) { labelErroForm.setText("Informe a data de início."); return; }

            Produto produto = livro != null ? livro : disco;
            Aluguel aluguel = new Aluguel(cliente, produto, inicio);

            if (dataDevolucao.getValue() != null) {
                aluguel.setDataDevolucao(dataDevolucao.getValue());
            }

            facade.registrarAluguel(aluguel);

            // Limpar formulário
            comboCliente.getSelectionModel().clearSelection();
            comboLivro.getSelectionModel().clearSelection();
            comboDisco.getSelectionModel().clearSelection();
            dataEmprestimo.setValue(LocalDate.now());
            dataDevolucao.setValue(null);
            labelValorTotal.setText("R$ 0,00");

            carregarAlugueis();

        } catch (Exception e) {
            labelErroForm.setText("Erro: " + e.getMessage());
        }
    }

    @FXML
    public void handleDevolucao() {
        LinhaAluguel selecionada = tabelaAlugueis.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            labelErro.setText("Selecione um aluguel na tabela para registrar devolução.");
            return;
        }
        if (selecionada.status.equals("Finalizado")) {
            labelErro.setText("Este aluguel já foi finalizado.");
            return;
        }

        // Busca o aluguel correspondente e finaliza
        List<Aluguel> alugueis = facade.listarAlugueis();
        for (Aluguel a : alugueis) {
            // Verifica se o aluguel bate com o cliente e título selecionado e ainda está Ativo
            boolean mesmoCliente = a.getCliente() != null && a.getCliente().getNome().equals(selecionada.nomeCliente);
            boolean mesmoProduto = a.getProdutos().stream().anyMatch(p -> p.getDescricao().contains(selecionada.titulo));
            
            if (mesmoCliente && mesmoProduto && !a.getStatus().equals("Finalizado")) {
                try {
                    facade.finalizarAluguel(a, LocalDate.now());
                    labelErro.setText("");
                    carregarAlugueis();
                    return;
                } catch (Exception e) {
                    labelErro.setText("Erro: " + e.getMessage());
                }
            }
        }
        labelErro.setText("Aluguel ativo não encontrado.");
    }

    // Classe auxiliar para linhas da tabela
    public static class LinhaAluguel {
        public final String nomeCliente, tipo, titulo, datas, status;
        public LinhaAluguel(String nomeCliente, String tipo, String titulo, String datas, String status) {
            this.nomeCliente = nomeCliente; this.tipo = tipo;
            this.titulo = titulo; this.datas = datas; this.status = status;
        }
    }

    @FXML public void abrirLivros() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLivros.fxml", "Duduteca - Livros"); }
    @FXML public void abrirDiscos() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDiscos.fxml", "Duduteca - Discos"); }
    @FXML public void abrirClientes() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaClientes.fxml", "Duduteca - Clientes"); }
    @FXML public void abrirAlugueis() { /* já estamos aqui */ }
    @FXML public void abrirDashboard() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml", "Duduteca - Dashboard"); }
    @FXML public void abrirRelatorio() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaRelatorio.fxml", "Duduteca - Relatório"); }
    @FXML public void handleSair() { SessaoUsuario.encerrarSessao(); navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLogin.fxml", "Duduteca - Login"); }

    private void navegar(String fxml, String titulo) {
        try { App.trocarTela(fxml, titulo); }
        catch (IOException e) { labelErro.setText("Tela não implementada: " + fxml); }
    }
}