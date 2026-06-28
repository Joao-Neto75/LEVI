package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.App;
import br.edu.ufersa.LEVI.model.entity.Livro;
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

public class LivrosController {

    // Sidebar
    @FXML private Button botaoLivros;
    @FXML private Button botaoDiscos;
    @FXML private Button botaoClientes;
    @FXML private Button botaoAlugueis;
    @FXML private Button botaoSair;

    // Filtros
    @FXML private TextField campoBuscaTitulo;
    @FXML private TextField campoBuscaAutor;
    @FXML private ComboBox<String> comboGenero;
    @FXML private Label labelErro;

    // Tabela
    @FXML private TableView<Livro> tabelaLivros;
    @FXML private TableColumn<Livro, String> colTitulo;
    @FXML private TableColumn<Livro, String> colAutor;
    @FXML private TableColumn<Livro, String> colGenero;
    @FXML private TableColumn<Livro, String> colAno;
    @FXML private TableColumn<Livro, String> colExemplares;
    @FXML private TableColumn<Livro, String> colValor;
    @FXML private TableColumn<Livro, String> colAcoes;

    // Formulário (dialog inline)
    @FXML private VBox painelFormulario;
    @FXML private Label labelTituloForm;
    @FXML private TextField fieldTitulo;
    @FXML private TextField fieldAutor;
    @FXML private TextField fieldGenero;
    @FXML private TextField fieldAno;
    @FXML private TextField fieldPaginas;
    @FXML private TextField fieldExemplares;
    @FXML private TextField fieldValor;
    @FXML private Label labelErroForm;
    @FXML private Button botaoFuncionarios;

    private final LocadoraFacade facade = new LocadoraFacade();
    private Livro livroEmEdicao = null;

    @FXML
    public void initialize() {
        configurarTabela();
        carregarGeneros();
        configurarAcessoPorCargo();
        carregarLivros(facade.listarLivros());
    }

    private void configurarTabela() {
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colAutor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAutor()));
        colGenero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenero()));
        colAno.setCellValueFactory(c -> {
            LocalDate ano = c.getValue().getAno();
            return new SimpleStringProperty(ano != null ? String.valueOf(ano.getYear()) : "—");
        });
        colExemplares.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getExemplares())));
        colValor.setCellValueFactory(c -> new SimpleStringProperty(String.format("R$ %.2f", c.getValue().getValorAluguel())));

        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("✏");
            private final Button btnDeletar = new Button("🗑");
            private final HBox box = new HBox(6, btnEditar, btnDeletar);

            {
                btnEditar.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
                btnDeletar.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");

                btnEditar.setOnAction(e -> {
                    Livro livro = getTableView().getItems().get(getIndex());
                    abrirFormularioEdicao(livro);
                });
                btnDeletar.setOnAction(e -> {
                    Livro livro = getTableView().getItems().get(getIndex());
                    confirmarExclusao(livro);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void carregarGeneros() {
        comboGenero.setItems(FXCollections.observableArrayList(
                "Todos", "Fantasia e Ficção", "Literatura Infanto-Juvenil", "Romance",
                "Terror", "Biografia", "Ciência", "História", "Autoajuda"
        ));
        comboGenero.getSelectionModel().selectFirst();
        comboGenero.setOnAction(e -> handleBuscar());
    }

    private void carregarLivros(List<Livro> livros) {
        ObservableList<Livro> obs = FXCollections.observableArrayList(livros);
        tabelaLivros.setItems(obs);
    }

    @FXML
    public void handleBuscar() {
        String titulo = campoBuscaTitulo.getText().trim();
        String autor = campoBuscaAutor.getText().trim();
        String genero = comboGenero.getValue();

        List<Livro> todos = facade.listarLivros();

        List<Livro> filtrados = todos.stream()
                .filter(l -> titulo.isEmpty() || l.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .filter(l -> autor.isEmpty() || l.getAutor().toLowerCase().contains(autor.toLowerCase()))
                .filter(l -> genero == null || genero.equals("Todos") || l.getGenero().equalsIgnoreCase(genero))
                .toList();

        carregarLivros(filtrados);
    }

    @FXML
    public void handleAdicionar() {
        livroEmEdicao = null;
        labelTituloForm.setText("Adicionar Livro");
        limparFormulario();
        mostrarFormulario(true);
    }

    private void abrirFormularioEdicao(Livro livro) {
        livroEmEdicao = livro;
        labelTituloForm.setText("Editar Livro");
        fieldTitulo.setText(livro.getTitulo());
        fieldAutor.setText(livro.getAutor());
        fieldGenero.setText(livro.getGenero());
        fieldAno.setText(livro.getAno() != null ? livro.getAno().toString() : "");
        fieldPaginas.setText(String.valueOf(livro.getPaginas()));
        fieldExemplares.setText(String.valueOf(livro.getExemplares()));
        fieldValor.setText(String.valueOf(livro.getValorAluguel()));
        mostrarFormulario(true);
    }

    @FXML
    public void handleSalvar() {
        labelErroForm.setText("");
        try {
            String titulo = fieldTitulo.getText().trim();
            String autor = fieldAutor.getText().trim();
            String genero = fieldGenero.getText().trim();
            String anoStr = fieldAno.getText().trim();
            int paginas = Integer.parseInt(fieldPaginas.getText().trim());
            int exemplares = Integer.parseInt(fieldExemplares.getText().trim());
            float valor = Float.parseFloat(fieldValor.getText().trim().replace(",", "."));
            LocalDate ano = anoStr.isEmpty() ? LocalDate.now() : LocalDate.parse(anoStr);

            if (livroEmEdicao == null) {
                Livro novo = new Livro(titulo, genero, ano, autor, paginas, exemplares, valor);
                facade.salvarLivro(novo);
            } else {
                livroEmEdicao.setTitulo(titulo);
                livroEmEdicao.setAutor(autor);
                livroEmEdicao.setGenero(genero);
                livroEmEdicao.setPaginas(paginas);
                livroEmEdicao.setExemplares(exemplares);
                livroEmEdicao.setValorAluguel(valor);
                facade.atualizarLivro(livroEmEdicao);
            }

            mostrarFormulario(false);
            carregarLivros(facade.listarLivros());

        } catch (NumberFormatException e) {
            labelErroForm.setText("Preencha corretamente páginas, exemplares e valor.");
        } catch (Exception e) {
            labelErroForm.setText("Erro: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancelar() {
        mostrarFormulario(false);
    }

    private void confirmarExclusao(Livro livro) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir \"" + livro.getTitulo() + "\"?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    facade.excluirLivro(livro);
                    carregarLivros(facade.listarLivros());
                } catch (Exception e) {
                    labelErro.setText("Erro ao excluir: " + e.getMessage());
                }
            }
        });
    }

    private void mostrarFormulario(boolean visivel) {
        painelFormulario.setVisible(visivel);
        painelFormulario.setManaged(visivel);
    }

    private void limparFormulario() {
        fieldTitulo.clear(); fieldAutor.clear(); fieldGenero.clear();
        fieldAno.clear(); fieldPaginas.clear(); fieldExemplares.clear();
        fieldValor.clear(); labelErroForm.setText("");
    }

    // ===== NAVEGAÇÃO =====
    @FXML public void abrirLivros() { /* já estamos aqui */ }
    @FXML public void abrirDiscos() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDiscos.fxml", "Duduteca - Discos"); }
    @FXML public void abrirClientes() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaClientes.fxml", "Duduteca - Clientes"); }
    @FXML public void abrirAlugueis() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaAlugueis.fxml", "Duduteca - Aluguéis"); }
    @FXML public void abrirDashboard() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml", "Duduteca - Dashboard"); }
    @FXML public void abrirFuncionarios() {
        if (!SessaoUsuario.isGerente()) return;
        navegar("/br/edu/ufersa/LEVI/view/fxml/TelaFuncionarios.fxml", "Duduteca - Funcionários");
    }
    @FXML public void abrirRelatorio() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaRelatorio.fxml", "Duduteca - Relatório"); }
    @FXML public void handleSair() {
        SessaoUsuario.encerrarSessao();
        navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLogin.fxml", "Duduteca - Login");
    }

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