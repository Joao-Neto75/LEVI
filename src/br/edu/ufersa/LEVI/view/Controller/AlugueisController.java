package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.App;
import br.edu.ufersa.LEVI.model.entity.*;
import br.edu.ufersa.LEVI.model.exception.ProdutoIndisponivelException;
import br.edu.ufersa.LEVI.model.service.LocadoraFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AlugueisController {

    // Formulário
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Livro>   comboLivro;
    @FXML private ComboBox<Disco>   comboDisco;
    @FXML private DatePicker dataEmprestimo;
    @FXML private DatePicker dataDevolucao;
    @FXML private Label labelValorTotal;
    @FXML private Label labelErroForm;
    @FXML private Label labelErro;

    // Lista de itens selecionados
    @FXML private VBox               boxItensSelecionados;
    @FXML private ListView<String>   listaItensSelecionados;

    // Tabela
    @FXML private TableView<LinhaAluguel> tabelaAlugueis;
    @FXML private TableColumn<LinhaAluguel, String> colCliente;
    @FXML private TableColumn<LinhaAluguel, String> colTipo;
    @FXML private TableColumn<LinhaAluguel, String> colTitulo;
    @FXML private TableColumn<LinhaAluguel, String> colDatas;
    @FXML private TableColumn<LinhaAluguel, String> colStatus;

    // Sidebar
    @FXML private Button botaoFuncionarios;

    private final LocadoraFacade facade = new LocadoraFacade();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yy");

    // Lista temporária de produtos que serão incluídos no aluguel
    private final List<Produto> itensPendentes = new ArrayList<>();
    private final ObservableList<String> nomesItensPendentes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarCombos();
        configurarAcessoPorCargo();
        configurarTabela();
        carregarAlugueis();
        listaItensSelecionados.setItems(nomesItensPendentes);
    }

    // ─── Configuração dos combos ────────────────────────────────────────────

    private void configurarCombos() {
        // Combo cliente
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

        // Combo livro
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
                setText(l == null || empty ? "" : l.getTitulo()
                        + " (" + l.getExemplares() + " disp.) — R$ " + l.getValorAluguel());
            }
        });

        // Combo disco
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
                setText(d == null || empty ? "" : d.getTitulo()
                        + " (" + d.getExemplares() + " disp.) — R$ " + d.getValorAluguel());
            }
        });

        dataEmprestimo.setValue(LocalDate.now());
    }

    // ─── Adicionar item à lista pendente ───────────────────────────────────

    @FXML
    public void handleAdicionarItem() {
        labelErroForm.setText("");
        Livro livro = comboLivro.getValue();
        Disco disco = comboDisco.getValue();

        if (livro == null && disco == null) {
            labelErroForm.setText("Selecione um livro ou disco para adicionar.");
            return;
        }

        // Adiciona livro se selecionado
        if (livro != null) {
            if (livro.getExemplares() <= 0) {
                labelErroForm.setText("Livro \"" + livro.getTitulo() + "\" sem exemplares disponíveis.");
                return;
            }
            // Verifica se já está na lista
            if (itensPendentes.contains(livro)) {
                labelErroForm.setText("\"" + livro.getTitulo() + "\" já está na lista.");
                return;
            }
            itensPendentes.add(livro);
            nomesItensPendentes.add("📖 " + livro.getTitulo()
                    + " — R$ " + String.format("%.2f", livro.getValorAluguel()));
            comboLivro.getSelectionModel().clearSelection();
        }

        // Adiciona disco se selecionado (pode adicionar junto com livro)
        if (disco != null) {
            if (disco.getExemplares() <= 0) {
                labelErroForm.setText("Disco \"" + disco.getTitulo() + "\" sem exemplares disponíveis.");
                return;
            }
            if (itensPendentes.contains(disco)) {
                labelErroForm.setText("\"" + disco.getTitulo() + "\" já está na lista.");
                return;
            }
            itensPendentes.add(disco);
            nomesItensPendentes.add("🎵 " + disco.getTitulo()
                    + " — R$ " + String.format("%.2f", disco.getValorAluguel()));
            comboDisco.getSelectionModel().clearSelection();
        }

        atualizarValorTotal();
        atualizarVisibilidadeLista();
    }

    @FXML
    public void handleLimparLista() {
        itensPendentes.clear();
        nomesItensPendentes.clear();
        atualizarValorTotal();
        atualizarVisibilidadeLista();
        labelErroForm.setText("");
    }

    private void atualizarValorTotal() {
        float total = (float) itensPendentes.stream()
                .mapToDouble(Produto::getValorAluguel).sum();
        labelValorTotal.setText(String.format("R$ %.2f", total));
    }

    private void atualizarVisibilidadeLista() {
        boolean temItens = !itensPendentes.isEmpty();
        boxItensSelecionados.setVisible(temItens);
        boxItensSelecionados.setManaged(temItens);
    }

    // ─── Registrar aluguel com todos os itens pendentes ────────────────────

    @FXML
    public void handleNovoAluguel() {
        labelErroForm.setText("");
        try {
            Cliente cliente = comboCliente.getValue();
            LocalDate inicio = dataEmprestimo.getValue();

            if (cliente == null) {
                labelErroForm.setText("Selecione um cliente.");
                return;
            }
            if (itensPendentes.isEmpty()) {
                labelErroForm.setText("Adicione ao menos um livro ou disco à lista.");
                return;
            }
            if (inicio == null) {
                labelErroForm.setText("Informe a data de início.");
                return;
            }

            // Cria o aluguel com o primeiro produto e adiciona os demais
            Aluguel aluguel = new Aluguel(cliente, itensPendentes.get(0), inicio);

            if (dataDevolucao.getValue() != null) {
                aluguel.setDataDevolucao(dataDevolucao.getValue());
            }

            // Adiciona os produtos restantes (a partir do índice 1)
            for (int i = 1; i < itensPendentes.size(); i++) {
                aluguel.adicionarProduto(itensPendentes.get(i));
            }

            facade.registrarAluguel(aluguel);

            // Limpa tudo
            comboCliente.getSelectionModel().clearSelection();
            dataEmprestimo.setValue(LocalDate.now());
            dataDevolucao.setValue(null);
            handleLimparLista();
            carregarAlugueis();

        } catch (ProdutoIndisponivelException e) {
            labelErroForm.setText("Produto indisponível: \""
                    + e.getTituloProduto() + "\" — sem exemplares.");
        } catch (Exception e) {
            labelErroForm.setText("Erro: " + e.getMessage());
        }
    }

    // ─── Tabela ────────────────────────────────────────────────────────────

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
                } else if ("Finalizado".equals(item)) {
                    setStyle("-fx-text-fill: #808080;");
                } else {
                    setStyle("-fx-text-fill: #228B22; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void carregarAlugueis() {
        List<LinhaAluguel> linhas = new ArrayList<>();
        for (Aluguel a : facade.listarAlugueis()) {
            String nomeCliente = a.getCliente() != null ? a.getCliente().getNome() : "—";
            String inicio = a.getDataEmprestimo() != null ? a.getDataEmprestimo().format(FMT) : "—";
            String fim    = a.getDataDevolucao()  != null ? a.getDataDevolucao().format(FMT)  : "—";
            String datas  = inicio + " - " + fim;

            String status = a.getStatus();
            if ("Ativo".equals(status) && a.getDataDevolucao() != null) {
                long atraso = java.time.temporal.ChronoUnit.DAYS
                        .between(a.getDataDevolucao(), LocalDate.now());
                if (atraso > 0) status = "Atrasado (" + atraso + " dias)";
            }

            for (Produto p : a.getProdutos()) {
                String tipo   = (p instanceof Livro) ? "Livro" : "Disco";
                String titulo = p.getTitulo();
                linhas.add(new LinhaAluguel(nomeCliente, tipo, titulo, datas, status));
            }
        }
        tabelaAlugueis.setItems(FXCollections.observableArrayList(linhas));
    }

    // ─── Devolução ─────────────────────────────────────────────────────────

    @FXML
    public void handleDevolucao() {
        LinhaAluguel selecionada = tabelaAlugueis.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            labelErro.setText("Selecione uma linha na tabela para registrar a devolução.");
            return;
        }
        if ("Finalizado".equals(selecionada.status)) {
            labelErro.setText("Este aluguel já foi finalizado.");
            return;
        }

        for (Aluguel a : facade.listarAlugueis()) {
            boolean mesmoCliente = a.getCliente() != null
                    && a.getCliente().getNome().equals(selecionada.nomeCliente);
            boolean mesmoProduto = a.getProdutos().stream()
                    .anyMatch(p -> p.getTitulo().equals(selecionada.titulo));

            if (mesmoCliente && mesmoProduto && !"Finalizado".equals(a.getStatus())) {
                try {
                    facade.finalizarAluguel(a, LocalDate.now());
                    labelErro.setText("");
                    carregarAlugueis();
                    return;
                } catch (Exception e) {
                    labelErro.setText("Erro: " + e.getMessage());
                    return;
                }
            }
        }
        labelErro.setText("Aluguel ativo não encontrado para devolução.");
    }

    // ─── Classe auxiliar ───────────────────────────────────────────────────

    public static class LinhaAluguel {
        public final String nomeCliente, tipo, titulo, datas, status;
        public LinhaAluguel(String nomeCliente, String tipo, String titulo,
                            String datas, String status) {
            this.nomeCliente = nomeCliente;
            this.tipo        = tipo;
            this.titulo      = titulo;
            this.datas       = datas;
            this.status      = status;
        }
    }

    // ─── Navegação ─────────────────────────────────────────────────────────

    @FXML public void abrirLivros()    { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLivros.fxml",        "Duduteca - Livros"); }
    @FXML public void abrirDiscos()    { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDiscos.fxml",        "Duduteca - Discos"); }
    @FXML public void abrirClientes()  { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaClientes.fxml",      "Duduteca - Clientes"); }
    @FXML public void abrirAlugueis()  { /* já estamos aqui */ }
    @FXML public void abrirDashboard() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml",     "Duduteca - Dashboard"); }
    @FXML public void abrirRelatorio() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaRelatorio.fxml",     "Duduteca - Relatório"); }
    @FXML public void abrirFuncionarios() {
        if (!SessaoUsuario.isGerente()) return;
        navegar("/br/edu/ufersa/LEVI/view/fxml/TelaFuncionarios.fxml", "Duduteca - Funcionários");
    }
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
