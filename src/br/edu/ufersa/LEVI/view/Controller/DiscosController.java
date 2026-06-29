package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.App;
import br.edu.ufersa.LEVI.model.entity.Disco;
import br.edu.ufersa.LEVI.model.service.LocadoraFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class DiscosController {

    @FXML private Button botaoLivros, botaoDiscos, botaoClientes, botaoAlugueis, botaoSair;

    @FXML private TextField campoBuscaTitulo;
    @FXML private TextField campoBuscaBanda;
    @FXML private ComboBox<String> comboEstilo;
    @FXML private Label labelErro;

    @FXML private TableView<Disco> tabelaDiscos;
    @FXML private TableColumn<Disco, String> colTitulo;
    @FXML private TableColumn<Disco, String> colBanda;
    @FXML private TableColumn<Disco, String> colEstilo;
    @FXML private TableColumn<Disco, String> colExemplares;
    @FXML private TableColumn<Disco, String> colAno;
    @FXML private TableColumn<Disco, String> colValor;
    @FXML private TableColumn<Disco, String> colAcoes;

    @FXML private VBox painelFormulario;
    @FXML private Label labelTituloForm;
    @FXML private TextField fieldTitulo, fieldBanda, fieldEstilo, fieldAno, fieldExemplares, fieldValor;
    @FXML private Label labelErroForm;
    @FXML private Button botaoFuncionarios;

    private final LocadoraFacade facade = new LocadoraFacade();
    private Disco discoEmEdicao = null;

    @FXML
    public void initialize() {
        configurarAcessoPorCargo();
        configurarTabela();
        carregarEstilos();
        carregarDiscos(facade.listarDiscos());
    }

    private void configurarTabela() {
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colBanda.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBanda()));
        colEstilo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstilo()));
        colExemplares.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getExemplares())));
        colAno.setCellValueFactory(c -> {
            LocalDate ano = c.getValue().getAno();
            return new SimpleStringProperty(ano != null ? String.valueOf(ano.getYear()) : "—");
        });
        colValor.setCellValueFactory(c -> new SimpleStringProperty(String.format("R$ %.2f", c.getValue().getValorAluguel())));

        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("✏");
            private final Button btnDeletar = new Button("🗑");
            private final HBox box = new HBox(6, btnEditar, btnDeletar);
            {
                btnEditar.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
                btnDeletar.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
                btnEditar.setOnAction(e -> abrirFormularioEdicao(getTableView().getItems().get(getIndex())));
                btnDeletar.setOnAction(e -> confirmarExclusao(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void carregarEstilos() {
        comboEstilo.setItems(FXCollections.observableArrayList(
                "Todos", "Rock", "Pop", "Jazz", "Clássico", "Samba", "Forró", "MPB", "Eletrônico"
        ));
        comboEstilo.getSelectionModel().selectFirst();
        comboEstilo.setOnAction(e -> handleBuscar());
    }

    private void carregarDiscos(List<Disco> discos) {
        tabelaDiscos.setItems(FXCollections.observableArrayList(discos));
    }

    @FXML
    public void handleBuscar() {
        String titulo = campoBuscaTitulo.getText().trim();
        String banda = campoBuscaBanda.getText().trim();
        String estilo = comboEstilo.getValue();

        List<Disco> filtrados = facade.listarDiscos().stream()
                .filter(d -> titulo.isEmpty() || d.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .filter(d -> banda.isEmpty() || d.getBanda().toLowerCase().contains(banda.toLowerCase()))
                .filter(d -> estilo == null || estilo.equals("Todos") || d.getEstilo().equalsIgnoreCase(estilo))
                .toList();

        carregarDiscos(filtrados);
    }

    @FXML
    public void handleAdicionar() {
        discoEmEdicao = null;
        labelTituloForm.setText("Adicionar Disco");
        limparFormulario();
        mostrarFormulario(true);
    }

    private void abrirFormularioEdicao(Disco disco) {
        discoEmEdicao = disco;
        labelTituloForm.setText("Editar Disco");
        fieldTitulo.setText(disco.getTitulo());
        fieldBanda.setText(disco.getBanda());
        fieldEstilo.setText(disco.getEstilo());
        fieldAno.setText(disco.getAno() != null ? disco.getAno().toString() : "");
        fieldExemplares.setText(String.valueOf(disco.getExemplares()));
        fieldValor.setText(String.valueOf(disco.getValorAluguel()));
        mostrarFormulario(true);
    }

    @FXML
    public void handleSalvar() {
        labelErroForm.setText("");
        try {
            String titulo = fieldTitulo.getText().trim();
            String banda = fieldBanda.getText().trim();
            String estilo = fieldEstilo.getText().trim();
            String anoStr = fieldAno.getText().trim();
            int exemplares = Integer.parseInt(fieldExemplares.getText().trim());
            float valor = Float.parseFloat(fieldValor.getText().trim().replace(",", "."));
            LocalDate ano = anoStr.isEmpty() ? LocalDate.now() : LocalDate.parse(anoStr);

            if (discoEmEdicao == null) {
                Disco novo = new Disco(titulo, banda, estilo, exemplares, valor, ano);
                facade.salvarDisco(novo);
            } else {
                discoEmEdicao.setTitulo(titulo);
                discoEmEdicao.setBanda(banda);
                discoEmEdicao.setEstilo(estilo);
                discoEmEdicao.setAno(ano);
                discoEmEdicao.setExemplares(exemplares);
                discoEmEdicao.setValorAluguel(valor);
                facade.atualizarDisco(discoEmEdicao);
            }

            mostrarFormulario(false);
            carregarDiscos(facade.listarDiscos());

        } catch (NumberFormatException e) {
            labelErroForm.setText("Preencha corretamente exemplares e valor.");
        } catch (Exception e) {
            labelErroForm.setText("Erro: " + e.getMessage());
        }
    }

    @FXML public void handleCancelar() { mostrarFormulario(false); }

    private void confirmarExclusao(Disco disco) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir \"" + disco.getTitulo() + "\"?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    facade.excluirDisco(disco);
                    carregarDiscos(facade.listarDiscos());
                } catch (Exception e) {
                    labelErro.setText("Erro ao excluir: " + e.getMessage());
                }
            }
        });
    }

    private void mostrarFormulario(boolean v) { painelFormulario.setVisible(v); painelFormulario.setManaged(v); }
    private void limparFormulario() {
        fieldTitulo.clear(); fieldBanda.clear(); fieldEstilo.clear();
        fieldAno.clear(); fieldExemplares.clear(); fieldValor.clear();
        labelErroForm.setText("");
    }

    @FXML public void abrirLivros() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLivros.fxml", "Duduteca - Livros"); }
    @FXML public void abrirDiscos() { /* já estamos aqui */ }
    @FXML public void abrirClientes() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaClientes.fxml", "Duduteca - Clientes"); }
    @FXML public void abrirAlugueis() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaAlugueis.fxml", "Duduteca - Aluguéis"); }
    @FXML public void abrirDashboard() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml", "Duduteca - Dashboard"); }
    @FXML public void abrirFuncionarios() {
        if (!SessaoUsuario.isGerente()) return;
        navegar("/br/edu/ufersa/LEVI/view/fxml/TelaFuncionarios.fxml", "Duduteca - Funcionários");
    }
    @FXML public void abrirRelatorio() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaRelatorio.fxml", "Duduteca - Relatório"); }
    @FXML public void handleSair() { SessaoUsuario.encerrarSessao(); navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLogin.fxml", "Duduteca - Login"); }

    private void navegar(String fxml, String titulo) {
        try { App.trocarTela(fxml, titulo); }
        catch (IOException e) { labelErro.setText("Tela não implementada: " + fxml); }
    }
    private void configurarAcessoPorCargo() {
        if (botaoFuncionarios != null) {
            boolean gerente = SessaoUsuario.isGerente();
            botaoFuncionarios.setVisible(gerente);
            botaoFuncionarios.setManaged(gerente);
        }
    }

}