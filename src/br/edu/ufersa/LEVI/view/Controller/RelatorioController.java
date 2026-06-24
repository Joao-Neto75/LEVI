package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.App;
import br.edu.ufersa.LEVI.model.entity.Aluguel;
import br.edu.ufersa.LEVI.model.entity.Cliente;
import br.edu.ufersa.LEVI.model.entity.Produto;
import br.edu.ufersa.LEVI.model.service.LocadoraFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class RelatorioController {

    // Sidebar
    @FXML private Button botaoLivros;
    @FXML private Button botaoDiscos;
    @FXML private Button botaoClientes;
    @FXML private Button botaoAlugueis;
    @FXML private Button botaoRelatorio;
    @FXML private Button botaoVoltar;

    // Card de faturamento
    @FXML private Label labelFaturamento;
    @FXML private Label labelLivrosAlugados;
    @FXML private Label labelDiscosAlugados;
    @FXML private ComboBox<String> comboMesAno;

    // Card de gerar relatório
    @FXML private ComboBox<String> comboFiltro;
    @FXML private TextField campoBuscaCliente;
    @FXML private Button botaoGerar;
    @FXML private Label labelErro;

    // Cabeçalho do cliente (some/aparece dependendo do filtro)
    @FXML private VBox boxCabecalhoCliente;
    @FXML private Label labelTituloRelatorioCliente;
    @FXML private Label labelDadosCliente;

    // Tabela
    @FXML private TableView<LinhaRelatorio> tabelaResultado;
    @FXML private TableColumn<LinhaRelatorio, String> colunaCliente;
    @FXML private TableColumn<LinhaRelatorio, String> colunaItem;
    @FXML private TableColumn<LinhaRelatorio, String> colunaDataInicio;
    @FXML private TableColumn<LinhaRelatorio, String> colunaDataFim;
    @FXML private TableColumn<LinhaRelatorio, String> colunaStatus;
    @FXML private TableColumn<LinhaRelatorio, String> colunaValor;

    private static final String FILTRO_TODO = "Todo";

    private final LocadoraFacade facade = new LocadoraFacade();

    private int mesSelecionado;
    private int anoSelecionado;

    @FXML
    public void initialize() {
        configurarColunasTabela();
        configurarComboMesAno();
        configurarComboFiltro();
        carregarFaturamentoDoMes();
    }

    private void configurarColunasTabela() {
        colunaCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colunaItem.setCellValueFactory(new PropertyValueFactory<>("item"));
        colunaDataInicio.setCellValueFactory(new PropertyValueFactory<>("dataInicioFormatada"));
        colunaDataFim.setCellValueFactory(new PropertyValueFactory<>("dataFimFormatada"));
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valorFormatado"));
    }

    private void configurarComboMesAno() {
        LocalDate hoje = LocalDate.now();
        mesSelecionado = hoje.getMonthValue();
        anoSelecionado = hoje.getYear();

        ObservableList<String> opcoes = FXCollections.observableArrayList();
        // gera os últimos 12 meses, do mais recente para o mais antigo
        LocalDate cursor = hoje;
        for (int i = 0; i < 12; i++) {
            opcoes.add(formatarMesAno(cursor.getMonthValue(), cursor.getYear()));
            cursor = cursor.minusMonths(1);
        }

        comboMesAno.setItems(opcoes);
        comboMesAno.getSelectionModel().selectFirst();
    }

    private void configurarComboFiltro() {
        comboFiltro.setItems(FXCollections.observableArrayList(FILTRO_TODO, "Cliente específico"));
        comboFiltro.getSelectionModel().select(FILTRO_TODO);
        atualizarEstadoCampoBusca();
    }

    @FXML
    public void handleMudarMesAno() {
        String selecionado = comboMesAno.getValue();
        if (selecionado == null) return;

        String[] partes = selecionado.split("/");
        mesSelecionado = mesPorNome(partes[0]);
        anoSelecionado = Integer.parseInt(partes[1]);

        carregarFaturamentoDoMes();
    }

    @FXML
    public void handleMudarFiltro() {
        atualizarEstadoCampoBusca();
    }

    private void atualizarEstadoCampoBusca() {
        boolean ehFiltroTodo = FILTRO_TODO.equals(comboFiltro.getValue());
        campoBuscaCliente.setDisable(ehFiltroTodo);
        if (ehFiltroTodo) {
            campoBuscaCliente.clear();
        }
    }

    @FXML
    public void handleGerarRelatorio() {
        labelErro.setText("");

        if (FILTRO_TODO.equals(comboFiltro.getValue())) {
            gerarRelatorioGeral();
        } else {
            gerarRelatorioPorCliente();
        }
    }

    // Filtro "Todo": mostra todos os aluguéis do mês selecionado, de todos os clientes
    private void gerarRelatorioGeral() {
        boxCabecalhoCliente.setVisible(false);
        boxCabecalhoCliente.setManaged(false);
        colunaCliente.setVisible(true);

        List<Aluguel> alugueis = facade.buscarAlugueisPorMes(mesSelecionado, anoSelecionado);
        montarTabela(alugueis);
    }

    // Filtro "Cliente específico": busca o cliente pelo nome/CPF digitado e mostra só os aluguéis dele
    private void gerarRelatorioPorCliente() {
        String termo = campoBuscaCliente.getText();

        if (termo == null || termo.trim().isEmpty()) {
            labelErro.setText("Digite o nome ou CPF do cliente para gerar o relatório.");
            return;
        }

        List<Cliente> encontrados = facade.pesquisarClientes(termo);

        if (encontrados.isEmpty()) {
            labelErro.setText("Nenhum cliente encontrado com esse nome ou CPF.");
            tabelaResultado.getItems().clear();
            boxCabecalhoCliente.setVisible(false);
            boxCabecalhoCliente.setManaged(false);
            return;
        }

        // usa o primeiro resultado encontrado; se houver mais de um, avisa no rótulo de erro
        Cliente cliente = encontrados.get(0);
        if (encontrados.size() > 1) {
            labelErro.setText("Mais de um cliente encontrado. Mostrando o relatório de: " + cliente.getNome());
        }

        exibirCabecalhoCliente(cliente);

        colunaCliente.setVisible(false);
        List<Aluguel> alugueis = facade.buscarAlugueisPorCliente(cliente);
        montarTabela(alugueis);
    }

    private void exibirCabecalhoCliente(Cliente cliente) {
        boxCabecalhoCliente.setVisible(true);
        boxCabecalhoCliente.setManaged(true);
        labelTituloRelatorioCliente.setText("Relatório de " + cliente.getNome());
        labelDadosCliente.setText("CPF: " + cliente.getCpf() + "    Endereço: " + cliente.getEndereco());
    }

    // Converte a lista de Aluguel (cada um com vários produtos) em linhas individuais para a tabela
    private void montarTabela(List<Aluguel> alugueis) {
        ObservableList<LinhaRelatorio> linhas = FXCollections.observableArrayList();

        for (Aluguel aluguel : alugueis) {
            String nomeCliente = aluguel.getCliente() != null ? aluguel.getCliente().getNome() : "—";

            for (Produto produto : aluguel.getProdutos()) {
                linhas.add(new LinhaRelatorio(
                        nomeCliente,
                        produto.getDescricao(),
                        aluguel.getDataEmprestimo(),
                        aluguel.getDataDevolucao(),
                        produto.getValorAluguel()
                ));
            }
        }

        tabelaResultado.setItems(linhas);
    }

    private void carregarFaturamentoDoMes() {
        float faturamento = facade.calcularFaturamentoMes(mesSelecionado, anoSelecionado);
        labelFaturamento.setText(String.format("R$ %.2f", faturamento));

        List<Aluguel> alugueis = facade.buscarAlugueisPorMes(mesSelecionado, anoSelecionado);
        int totalLivros = 0;
        int totalDiscos = 0;

        for (Aluguel aluguel : alugueis) {
            for (Produto produto : aluguel.getProdutos()) {
                // Distingue livro de disco pela classe concreta, já que Produto é abstrata
                if (produto.getClass().getSimpleName().equalsIgnoreCase("Livro")) {
                    totalLivros++;
                } else {
                    totalDiscos++;
                }
            }
        }

        labelLivrosAlugados.setText("Livros: " + totalLivros);
        labelDiscosAlugados.setText("Discos: " + totalDiscos);
    }

    private String formatarMesAno(int mes, int ano) {
        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        return meses[mes - 1] + "/" + ano;
    }

    private int mesPorNome(String abreviacao) {
        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        for (int i = 0; i < meses.length; i++) {
            if (meses[i].equalsIgnoreCase(abreviacao)) {
                return i + 1;
            }
        }
        return LocalDate.now().getMonthValue();
    }

    // ===================== NAVEGAÇÃO =====================

    @FXML
    public void abrirLivros() {
        trocarTelaComTratamento("/br/edu/ufersa/LEVI/view/fxml/TelaLivros.fxml", "Duduteca - Livros");
    }

    @FXML
    public void abrirDiscos() {
        trocarTelaComTratamento("/br/edu/ufersa/LEVI/view/fxml/TelaDiscos.fxml", "Duduteca - Discos");
    }

    @FXML
    public void abrirClientes() {
        trocarTelaComTratamento("/br/edu/ufersa/LEVI/view/fxml/TelaClientes.fxml", "Duduteca - Clientes");
    }

    @FXML
    public void abrirAlugueis() {
        trocarTelaComTratamento("/br/edu/ufersa/LEVI/view/fxml/TelaAlugueis.fxml", "Duduteca - Aluguéis");
    }

    @FXML
    public void abrirRelatorio() {
        // já estamos na tela de relatório; não faz nada
    }

    @FXML
    public void abrirDashboard() {
        trocarTelaComTratamento("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml", "Duduteca - Dashboard");
    }

    @FXML
    public void handleSair() {
        SessaoUsuario.encerrarSessao();
        trocarTelaComTratamento("/br/edu/ufersa/LEVI/view/fxml/TelaLogin.fxml", "Duduteca - Login");
    }

    private void trocarTelaComTratamento(String caminhoFxml, String titulo) {
        try {
            App.trocarTela(caminhoFxml, titulo);
        } catch (IOException e) {
            labelErro.setText("Tela ainda não implementada: " + caminhoFxml);
        }
    }
}
