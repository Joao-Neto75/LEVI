package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.App;
import br.edu.ufersa.LEVI.model.entity.Aluguel;
import br.edu.ufersa.LEVI.model.entity.Cliente;
import br.edu.ufersa.LEVI.model.exception.AluguelAtivoException;
import br.edu.ufersa.LEVI.model.service.LocadoraFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ClientesController {

    @FXML private TextField campoBuscaNome, campoBuscaCpf;
    @FXML private Label labelErro;

    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, String> colNome, colCpf, colEndereco, colStatus, colItens, colAcoes;

    @FXML private Button botaoFuncionarios;
    @FXML private StackPane painelFormulario;
    @FXML private Label labelTituloForm;
    @FXML private TextField fieldNome, fieldCpf, fieldEndereco;
    @FXML private Label labelErroForm;

    private final LocadoraFacade facade = new LocadoraFacade();
    private Cliente clienteEmEdicao = null;

    @FXML
    public void initialize() {
        configurarAcessoPorCargo();
        configurarTabela();
        carregarClientes(facade.listarClientes());
    }

    private void configurarTabela() {
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colCpf.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCpf()));
        colEndereco.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndereco()));

        colStatus.setCellValueFactory(c -> {
            Cliente cliente = c.getValue();
            List<Aluguel> alugueis = facade.buscarAlugueisPorCliente(cliente);
            boolean atrasado = alugueis.stream().anyMatch(a ->
                    a.getDataDevolucao() != null
                    && a.getDataDevolucao().isBefore(LocalDate.now())
                    && !"Finalizado".equals(a.getStatus())
            );
            return new SimpleStringProperty(atrasado ? "Atrasado" : "Regular");
        });

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Atrasado".equals(item)
                        ? "-fx-text-fill: #cc0000; -fx-font-weight: bold;"
                        : "-fx-text-fill: #228B22; -fx-font-weight: bold;");
            }
        });

        colItens.setCellValueFactory(c -> {
            List<Aluguel> alugueis = facade.buscarAlugueisPorCliente(c.getValue());
            int total = alugueis.stream()
                    .filter(a -> !"Finalizado".equals(a.getStatus()))
                    .mapToInt(a -> a.getProdutos().size())
                    .sum();
            return new SimpleStringProperty(total == 0 ? "0" : String.valueOf(total));
        });

        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar    = new Button("✏");
            private final Button btnHistorico = new Button("📋");
            private final Button btnExcluir   = new Button("🗑");
            private final HBox box = new HBox(6, btnHistorico, btnEditar, btnExcluir);
            {
                btnEditar.setStyle("-fx-background-color: transparent; -fx-font-size: 15px; -fx-cursor: hand;");
                btnHistorico.setStyle("-fx-background-color: transparent; -fx-font-size: 15px; -fx-cursor: hand;");
                btnExcluir.setStyle("-fx-background-color: transparent; -fx-font-size: 15px; -fx-cursor: hand;");
                btnHistorico.setTooltip(new Tooltip("Ver histórico"));
                btnEditar.setTooltip(new Tooltip("Editar cliente"));
                btnExcluir.setTooltip(new Tooltip("Excluir cliente"));

                btnEditar.setOnAction(e -> {
                    Cliente c = getTableView().getItems().get(getIndex());
                    abrirFormularioEdicao(c);
                });
                btnHistorico.setOnAction(e -> {
                    Cliente c = getTableView().getItems().get(getIndex());
                    abrirHistorico(c);
                });
                btnExcluir.setOnAction(e -> {
                    Cliente c = getTableView().getItems().get(getIndex());
                    confirmarExclusao(c);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tabelaClientes.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                List<Aluguel> alugueis = facade.buscarAlugueisPorCliente(item);
                boolean atrasado = alugueis.stream().anyMatch(a ->
                        a.getDataDevolucao() != null
                        && a.getDataDevolucao().isBefore(LocalDate.now())
                        && !"Finalizado".equals(a.getStatus())
                );
                setStyle(atrasado ? "-fx-background-color: #fff0f0;" : "");
            }
        });
    }

    private void abrirHistorico(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/br/edu/ufersa/LEVI/view/fxml/TelaHistoricoCliente.fxml")
            );
            Parent root = loader.load();

            HistoricoClienteController controller = loader.getController();
            controller.carregarCliente(cliente);

            Stage popup = new Stage();
            popup.setTitle("Histórico — " + cliente.getNome());
            popup.setScene(new Scene(root, 700, 480));
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(tabelaClientes.getScene().getWindow());
            popup.setResizable(true);
            popup.show();

        } catch (IOException e) {
            labelErro.setText("Erro ao abrir histórico: " + e.getMessage());
        }
    }

    private void carregarClientes(List<Cliente> clientes) {
        tabelaClientes.setItems(FXCollections.observableArrayList(clientes));
    }

    @FXML
    public void handleBuscar() {
        String nome = campoBuscaNome.getText().trim();
        String cpf  = campoBuscaCpf.getText().trim();
        String termo = !nome.isEmpty() ? nome : cpf;
        carregarClientes(termo.isEmpty() ? facade.listarClientes() : facade.pesquisarClientes(termo));
    }

    @FXML
    public void handleAdicionar() {
        clienteEmEdicao = null;
        labelTituloForm.setText("Adicionar Cliente");
        fieldNome.clear(); fieldCpf.clear(); fieldEndereco.clear();
        labelErroForm.setText("");
        mostrarFormulario(true);
    }

    private void abrirFormularioEdicao(Cliente cliente) {
        clienteEmEdicao = cliente;
        labelTituloForm.setText("Editar Cliente");
        fieldNome.setText(cliente.getNome());
        fieldCpf.setText(cliente.getCpf());
        fieldEndereco.setText(cliente.getEndereco());
        labelErroForm.setText("");
        mostrarFormulario(true);
    }

    @FXML
    public void handleSalvar() {
        labelErroForm.setText("");
        try {
            String nome     = fieldNome.getText().trim();
            String cpf      = fieldCpf.getText().trim();
            String endereco = fieldEndereco.getText().trim();

            if (nome.isEmpty() || cpf.isEmpty()) {
                labelErroForm.setText("Nome e CPF são obrigatórios.");
                return;
            }

            if (clienteEmEdicao == null) {
                facade.salvarCliente(new Cliente(nome, endereco, cpf));
            } else {
                clienteEmEdicao.atualizarNome(nome);
                clienteEmEdicao.setCpf(cpf);
                clienteEmEdicao.atualizarEndereco(endereco);
                facade.atualizarCliente(clienteEmEdicao);
            }

            mostrarFormulario(false);
            carregarClientes(facade.listarClientes());

        } catch (Exception e) {
            labelErroForm.setText("Erro: " + e.getMessage());
        }
    }

    private void confirmarExclusao(Cliente cliente) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir o cliente \"" + cliente.getNome() + "\"?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar exclusão");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    facade.excluirCliente(cliente);
                    carregarClientes(facade.listarClientes());
                    labelErro.setText("");
                } catch (AluguelAtivoException e) {
                    labelErro.setText(e.getMessage());
                } catch (Exception e) {
                    labelErro.setText("Erro ao excluir: " + e.getMessage());
                }
            }
        });
    }

    @FXML public void handleCancelar() { mostrarFormulario(false); }

    private void mostrarFormulario(boolean v) {
        painelFormulario.setVisible(v);
        painelFormulario.setManaged(v);
    }

    @FXML public void abrirLivros()    { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLivros.fxml",    "Duduteca - Livros"); }
    @FXML public void abrirDiscos()    { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDiscos.fxml",    "Duduteca - Discos"); }
    @FXML public void abrirClientes()  { /* já estamos aqui */ }
    @FXML public void abrirAlugueis()  { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaAlugueis.fxml", "Duduteca - Aluguéis"); }
    @FXML public void abrirDashboard() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml","Duduteca - Dashboard"); }
    @FXML public void abrirFuncionarios() {
        if (!SessaoUsuario.isGerente()) return;
        navegar("/br/edu/ufersa/LEVI/view/fxml/TelaFuncionarios.fxml", "Duduteca - Funcionários");
    }
    @FXML public void abrirRelatorio() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaRelatorio.fxml","Duduteca - Relatório"); }
    @FXML public void handleSair()     { SessaoUsuario.encerrarSessao(); navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLogin.fxml", "Duduteca - Login"); }

    private void navegar(String fxml, String titulo) {
        try { App.trocarTela(fxml, titulo); }
        catch (IOException e) { labelErro.setText("Erro ao navegar: " + e.getMessage()); }
    }
    private void configurarAcessoPorCargo() {
        if (botaoFuncionarios != null) {
            boolean gerente = SessaoUsuario.isGerente();
            botaoFuncionarios.setVisible(gerente);
            botaoFuncionarios.setManaged(gerente);
        }
    }

}
