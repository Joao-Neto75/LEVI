package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.App;
import br.edu.ufersa.LEVI.model.entity.Funcionarios;
import br.edu.ufersa.LEVI.model.service.LocadoraFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FuncionariosController {

    @FXML private TextField campoBusca;
    @FXML private Label labelErro;

    @FXML private TableView<Funcionarios> tabelaFuncionarios;
    @FXML private TableColumn<Funcionarios, String> colNome, colCargo, colEmail, colContratacao, colAcoes;

    @FXML private StackPane painelFormulario;
    @FXML private Label labelTituloForm, labelErroForm;
    @FXML private TextField fieldNome, fieldEmail;
    @FXML private PasswordField fieldSenha, fieldConfirma;
    @FXML private ComboBox<String> comboCargo;

    private final LocadoraFacade facade = new LocadoraFacade();
    private final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
    private Funcionarios funcionarioEmEdicao = null;

    @FXML
    public void initialize() {
        // Bloqueia acesso se não for gerente
        if (!SessaoUsuario.isGerente()) {
            navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml", "Duduteca - Dashboard");
            return;
        }

        comboCargo.setItems(FXCollections.observableArrayList("Gerente", "Atendente"));
        configurarTabela();
        carregarFuncionarios(facade.listarFuncionarios());
    }

    private void configurarTabela() {
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colCargo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCargo()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colContratacao.setCellValueFactory(c -> {
            Date d = c.getValue().getContratacao();
            return new SimpleStringProperty(d != null ? SDF.format(d) : "—");
        });

        // Colorir cargo: Gerente em dourado, Atendente normal
        colCargo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Gerente".equalsIgnoreCase(item)
                        ? "-fx-font-weight: bold; -fx-text-fill: #b8860b;"
                        : "-fx-text-fill: #333;");
            }
        });

        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar  = new Button("✏");
            private final Button btnExcluir = new Button("🗑");
            private final HBox box = new HBox(6, btnEditar, btnExcluir);
            {
                btnEditar.setStyle("-fx-background-color: transparent; -fx-font-size: 15px; -fx-cursor: hand;");
                btnExcluir.setStyle("-fx-background-color: transparent; -fx-font-size: 15px; -fx-cursor: hand;");

                btnEditar.setOnAction(e -> abrirEdicao(getTableView().getItems().get(getIndex())));
                btnExcluir.setOnAction(e -> {
                    Funcionarios f = getTableView().getItems().get(getIndex());
                    // Não deixa excluir a si mesmo
                    if (f.getId() == SessaoUsuario.getFuncionarioLogado().getId()) {
                        labelErro.setText("Você não pode excluir sua própria conta.");
                        return;
                    }
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Excluir funcionário \"" + f.getNome() + "\"?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Confirmar exclusão");
                    confirm.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.YES) {
                            facade.excluirFuncionario(f);
                            carregarFuncionarios(facade.listarFuncionarios());
                        }
                    });
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                // Não mostra botão de excluir para si mesmo
                Funcionarios f = getTableView().getItems().get(getIndex());
                btnExcluir.setDisable(f.getId() == SessaoUsuario.getFuncionarioLogado().getId());
                setGraphic(box);
            }
        });
    }

    private void carregarFuncionarios(List<Funcionarios> lista) {
        tabelaFuncionarios.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    public void handleBuscar() {
        String termo = campoBusca.getText().trim();
        carregarFuncionarios(termo.isEmpty()
                ? facade.listarFuncionarios()
                : facade.buscarFuncionarios(termo));
    }

    @FXML
    public void handleAdicionar() {
        funcionarioEmEdicao = null;
        labelTituloForm.setText("Novo Funcionário");
        fieldNome.clear(); fieldEmail.clear();
        fieldSenha.clear(); fieldConfirma.clear();
        comboCargo.setValue(null);
        fieldSenha.setPromptText("Senha");
        fieldSenha.setDisable(false);
        fieldConfirma.setDisable(false);
        labelErroForm.setText("");
        mostrarFormulario(true);
    }

    private void abrirEdicao(Funcionarios f) {
        funcionarioEmEdicao = f;
        labelTituloForm.setText("Editar Funcionário");
        fieldNome.setText(f.getNome());
        fieldEmail.setText(f.getEmail());
        comboCargo.setValue(f.getCargo());
        // Na edição, senha em branco = não alterar
        fieldSenha.clear();
        fieldConfirma.clear();
        fieldSenha.setPromptText("Nova senha (deixe em branco para não alterar)");
        labelErroForm.setText("");
        mostrarFormulario(true);
    }

    @FXML
    public void handleSalvar() {
        labelErroForm.setText("");
        try {
            String nome   = fieldNome.getText().trim();
            String email  = fieldEmail.getText().trim();
            String cargo  = comboCargo.getValue();
            String senha  = fieldSenha.getText();
            String confirma = fieldConfirma.getText();

            if (nome.isEmpty() || email.isEmpty() || cargo == null) {
                labelErroForm.setText("Nome, e-mail e cargo são obrigatórios.");
                return;
            }

            if (funcionarioEmEdicao == null) {
                // Novo: senha obrigatória
                if (senha.isEmpty()) {
                    labelErroForm.setText("Informe uma senha para o novo funcionário.");
                    return;
                }
                if (!senha.equals(confirma)) {
                    labelErroForm.setText("As senhas não coincidem.");
                    return;
                }
                Funcionarios novo = new Funcionarios(nome, cargo, 0, new Date(), email, senha);
                facade.cadastrarFuncionario(novo);
            } else {
                // Edição
                funcionarioEmEdicao.setNome(nome);
                funcionarioEmEdicao.setEmail(email);
                funcionarioEmEdicao.setCargo(cargo);
                if (!senha.isEmpty()) {
                    if (!senha.equals(confirma)) {
                        labelErroForm.setText("As senhas não coincidem.");
                        return;
                    }
                    funcionarioEmEdicao.setSenha(senha);
                }
                facade.atualizarFuncionario(funcionarioEmEdicao);
            }

            mostrarFormulario(false);
            carregarFuncionarios(facade.listarFuncionarios());

        } catch (Exception e) {
            labelErroForm.setText("Erro: " + e.getMessage());
        }
    }

    @FXML public void handleCancelar() { mostrarFormulario(false); }

    private void mostrarFormulario(boolean v) {
        painelFormulario.setVisible(v);
        painelFormulario.setManaged(v);
    }

    @FXML public void abrirLivros()        { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLivros.fxml",        "Duduteca - Livros"); }
    @FXML public void abrirDiscos()        { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDiscos.fxml",        "Duduteca - Discos"); }
    @FXML public void abrirClientes()      { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaClientes.fxml",      "Duduteca - Clientes"); }
    @FXML public void abrirAlugueis()      { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaAlugueis.fxml",      "Duduteca - Aluguéis"); }
    @FXML public void abrirRelatorio()     { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaRelatorio.fxml",     "Duduteca - Relatório"); }
    @FXML public void abrirFuncionarios()  { /* já estamos aqui */ }
    @FXML public void abrirDashboard()     { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml",     "Duduteca - Dashboard"); }
    @FXML public void handleSair()         { SessaoUsuario.encerrarSessao(); navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLogin.fxml", "Duduteca - Login"); }

    private void navegar(String fxml, String titulo) {
        try { App.trocarTela(fxml, titulo); }
        catch (IOException e) { labelErro.setText("Erro ao navegar: " + e.getMessage()); }
    }
}
