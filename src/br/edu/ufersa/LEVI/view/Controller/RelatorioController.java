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
import javafx.util.StringConverter;

import java.awt.Desktop;
import java.io.File;
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
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private Button botaoGerar;
    @FXML private Button botaoGerarPdf;
    @FXML private Label labelErro;
    @FXML private Label labelSucesso;

    // Cabeçalho do cliente (some/aparece dependendo do filtro)
    @FXML private VBox boxCabecalhoCliente;
    @FXML private Label labelTituloRelatorioCliente;
    @FXML private Label labelDadosCliente;

    // Tabela (prévia em tela, antes de exportar o PDF)
    @FXML private TableView<LinhaRelatorio> tabelaResultado;
    @FXML private TableColumn<LinhaRelatorio, String> colunaCliente;
    @FXML private TableColumn<LinhaRelatorio, String> colunaItem;
    @FXML private TableColumn<LinhaRelatorio, String> colunaDataInicio;
    @FXML private TableColumn<LinhaRelatorio, String> colunaDataFim;
    @FXML private TableColumn<LinhaRelatorio, String> colunaStatus;
    @FXML private TableColumn<LinhaRelatorio, String> colunaValor;

    private static final String FILTRO_TODO = "Todo";

    private final LocadoraFacade facade = new LocadoraFacade();
    private final GeradorPdfRelatorio geradorPdf = new GeradorPdfRelatorio();

    private int mesSelecionado;
    private int anoSelecionado;

    // guarda o último resultado gerado, para o botão de PDF usar sem recalcular
    private List<LinhaRelatorio> ultimasLinhasGeradas = List.of();
    private String ultimoTituloGerado = "";
    private String ultimoSubtituloGerado = "";

    @FXML
    public void initialize() {
        configurarColunasTabela();
        configurarComboMesAno();
        configurarComboFiltro();
        configurarComboCliente();
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
        LocalDate cursor = hoje;
        for (int i = 0; i < 12; i++) {
            opcoes.add(formatarMesAno(cursor.getMonthValue(), cursor.getYear()));
            cursor = cursor.minusMonths(1);
        }

        configurarAcessoPorCargo();
        comboMesAno.setItems(opcoes);
        comboMesAno.getSelectionModel().selectFirst();
    }

    private void configurarComboFiltro() {
        comboFiltro.setItems(FXCollections.observableArrayList(FILTRO_TODO, "Cliente específico"));
        comboFiltro.getSelectionModel().select(FILTRO_TODO);
        atualizarEstadoComboCliente();
    }

    // Carrega todos os clientes do banco no ComboBox, exibindo "Nome — CPF"
    private void configurarComboCliente() {
        List<Cliente> clientes = facade.listarClientes();
        comboCliente.setItems(FXCollections.observableArrayList(clientes));

        comboCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente cliente) {
                if (cliente == null) return "";
                return cliente.getNome() + " — " + cliente.getCpf();
            }

            @Override
            public Cliente fromString(String string) {
                // não é necessário converter de volta: o ComboBox não é editável
                return null;
            }
        });
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
        atualizarEstadoComboCliente();
    }

    private void atualizarEstadoComboCliente() {
        boolean ehFiltroTodo = FILTRO_TODO.equals(comboFiltro.getValue());
        comboCliente.setDisable(ehFiltroTodo);
        if (ehFiltroTodo) {
            comboCliente.getSelectionModel().clearSelection();
        }
    }

    @FXML
    public void handleGerarRelatorio() {
        labelErro.setText("");
        labelSucesso.setText("");

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
        List<LinhaRelatorio> linhas = montarLinhas(alugueis);
        tabelaResultado.setItems(FXCollections.observableArrayList(linhas));

        ultimasLinhasGeradas = linhas;
        ultimoTituloGerado = "Relatório Geral de Aluguéis";
        ultimoSubtituloGerado = "Período: " + comboMesAno.getValue();
    }

    // Filtro "Cliente específico": usa o cliente escolhido no ComboBox
    private void gerarRelatorioPorCliente() {
        Cliente cliente = comboCliente.getValue();

        if (cliente == null) {
            labelErro.setText("Selecione um cliente para gerar o relatório.");
            return;
        }

        exibirCabecalhoCliente(cliente);

        colunaCliente.setVisible(false);
        List<Aluguel> alugueis = facade.buscarAlugueisPorCliente(cliente);
        List<LinhaRelatorio> linhas = montarLinhas(alugueis);
        tabelaResultado.setItems(FXCollections.observableArrayList(linhas));

        ultimasLinhasGeradas = linhas;
        ultimoTituloGerado = "Relatório de " + cliente.getNome();
        ultimoSubtituloGerado = "CPF: " + cliente.getCpf() + "    Endereço: " + cliente.getEndereco();
    }

    private void exibirCabecalhoCliente(Cliente cliente) {
        boxCabecalhoCliente.setVisible(true);
        boxCabecalhoCliente.setManaged(true);
        labelTituloRelatorioCliente.setText("Relatório de " + cliente.getNome());
        labelDadosCliente.setText("CPF: " + cliente.getCpf() + "    Endereço: " + cliente.getEndereco());
    }

    // Converte a lista de Aluguel (cada um com vários produtos) em linhas individuais
    private List<LinhaRelatorio> montarLinhas(List<Aluguel> alugueis) {
        List<LinhaRelatorio> linhas = new java.util.ArrayList<>();

        for (Aluguel aluguel : alugueis) {
            String nomeCliente = aluguel.getCliente() != null ? aluguel.getCliente().getNome() : "—";

            for (Produto produto : aluguel.getProdutos()) {
                linhas.add(new LinhaRelatorio(
                        nomeCliente,
                        produto.getDescricao(),
                        aluguel.getDataEmprestimo(),
                        aluguel.getDataDevolucao(),
                        aluguel.getStatus(),
                        produto.getValorAluguel()
                ));
            }
        }
        return linhas;
    }

    // Gera o PDF a partir do último relatório exibido na tela e salva em Downloads
    @FXML
    public void handleGerarPdf() {
        labelErro.setText("");
        labelSucesso.setText("");

        if (ultimasLinhasGeradas.isEmpty() && tabelaResultado.getItems().isEmpty()) {
            labelErro.setText("Gere o relatório na tela antes de exportar o PDF.");
            return;
        }

        float faturamentoPeriodo = 0;
        for (LinhaRelatorio linha : ultimasLinhasGeradas) {
            faturamentoPeriodo += linha.getValor();
        }

        try {
            File arquivo = geradorPdf.gerar(ultimoTituloGerado, ultimoSubtituloGerado,
                    ultimasLinhasGeradas, faturamentoPeriodo);

            labelSucesso.setText("PDF salvo em: " + arquivo.getAbsolutePath());

            // Abre o PDF automaticamente no leitor padrão do sistema, se disponível
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(arquivo);
            }

        } catch (IOException e) {
            labelErro.setText("Erro ao gerar o PDF: " + e.getMessage());
        }
    }

    private void carregarFaturamentoDoMes() {
        float faturamento = facade.calcularFaturamentoMes(mesSelecionado, anoSelecionado);
        labelFaturamento.setText(String.format("R$ %.2f", faturamento));

        List<Aluguel> alugueis = facade.buscarAlugueisPorMes(mesSelecionado, anoSelecionado);
        int totalLivros = 0;
        int totalDiscos = 0;

        for (Aluguel aluguel : alugueis) {
            for (Produto produto : aluguel.getProdutos()) {
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

    @FXML private Button botaoFuncionarios;

    @FXML
    public void abrirRelatorio() {
        // já estamos na tela de relatório; não faz nada
    }

    @FXML
    public void abrirDashboard() {
        trocarTelaComTratamento("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml", "Duduteca - Dashboard");
    }

    @FXML
    public void abrirFuncionarios() {
        if (!SessaoUsuario.isGerente()) return;
        trocarTelaComTratamento("/br/edu/ufersa/LEVI/view/fxml/TelaFuncionarios.fxml", "Duduteca - Funcionários");
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
    private void configurarAcessoPorCargo() {
        if (botaoFuncionarios != null) {
            boolean gerente = SessaoUsuario.isGerente();
            botaoFuncionarios.setVisible(gerente);
            botaoFuncionarios.setManaged(gerente);
        }
    }

}
