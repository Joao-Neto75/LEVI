package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.App;
import br.edu.ufersa.LEVI.model.entity.Aluguel;
import br.edu.ufersa.LEVI.model.entity.Disco;
import br.edu.ufersa.LEVI.model.entity.Funcionarios;
import br.edu.ufersa.LEVI.model.entity.Livro;
import br.edu.ufersa.LEVI.model.service.LocadoraFacade;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    @FXML private Button botaoLivros;
    @FXML private Button botaoDiscos;
    @FXML private Button botaoClientes;
    @FXML private Button botaoAlugueis;
    @FXML private Button botaoSair;
    @FXML private Button botaoFuncionarios;  // visível só para Gerente

    @FXML private TextField campoBusca;
    @FXML private Label labelFuncionarioLogado;
    @FXML private Button botaoRelatorio;

    @FXML private ListView<String> listaMaisBuscados;
    @FXML private ListView<String> listaAlugueisExpirando;
    @FXML private ListView<String> listaRenovacoes;
    @FXML private Label labelVisaoGeralTitulo;
    @FXML private Label labelFaturamento;
    @FXML private Label labelAlugueisAtivos;
    @FXML private Label labelEstoque;

    private final LocadoraFacade facade = new LocadoraFacade();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yy");

    @FXML
    public void initialize() {
        exibirFuncionarioLogado();
        configurarAcessoPorCargo();
        carregarVisaoGeral();
        carregarMaisBuscados();
        carregarAlugueisExpirando();
        carregarRenovacoes();
    }

    // Mostra nome do funcionário logado e exibe/oculta botão de Funcionários
    private void exibirFuncionarioLogado() {
        Funcionarios logado = SessaoUsuario.getFuncionarioLogado();
        if (logado != null && labelFuncionarioLogado != null) {
            labelFuncionarioLogado.setText(logado.getNome() + "\n" + logado.getCargo());
        }
    }

    private void configurarAcessoPorCargo() {
        boolean gerente = SessaoUsuario.isGerente();
        if (botaoFuncionarios != null) {
            botaoFuncionarios.setVisible(gerente);
            botaoFuncionarios.setManaged(gerente);
        }
    }

    private void carregarVisaoGeral() {
        LocalDate hoje = LocalDate.now();

        float faturamento = facade.calcularFaturamentoMes(hoje.getMonthValue(), hoje.getYear());
        labelFaturamento.setText(String.format("Faturamento Total: R$ %.2f", faturamento));

        int totalLivros = facade.listarLivros().size();
        int totalDiscos = facade.listarDiscos().size();
        labelEstoque.setText(String.format("Total em Estoque: %d (%d livros, %d discos)",
                totalLivros + totalDiscos, totalLivros, totalDiscos));

        long alugueisAtivos = facade.listarAlugueis().stream()
                .filter(a -> "Ativo".equals(a.getStatus()))
                .count();
        labelAlugueisAtivos.setText("Aluguéis Ativos: " + alugueisAtivos);

        labelVisaoGeralTitulo.setText("Visão Geral ("
                + mesEmPortugues(hoje.getMonthValue()) + "/" + hoje.getYear() + ")");
    }

    private void carregarMaisBuscados() {
        List<String> itens = new ArrayList<>();

        facade.listarLivros().stream()
                .sorted((a, b) -> Integer.compare(b.getExemplares(), a.getExemplares()))
                .limit(3)
                .forEach(l -> itens.add("📖 " + l.getTitulo() + " — " + l.getAutor()));

        facade.listarDiscos().stream()
                .sorted((a, b) -> Integer.compare(b.getExemplares(), a.getExemplares()))
                .limit(2)
                .forEach(d -> itens.add("🎵 " + d.getTitulo() + " — " + d.getBanda()));

        if (itens.isEmpty()) itens.add("Nenhum item cadastrado ainda.");
        listaMaisBuscados.setItems(FXCollections.observableArrayList(itens));
    }

    private void carregarAlugueisExpirando() {
        List<String> itens = new ArrayList<>();
        LocalDate hoje  = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);

        for (Aluguel a : facade.listarAlugueis()) {
            if (!"Ativo".equals(a.getStatus())) continue;

            LocalDate dataRef = a.getDataPrevistaDevolucao() != null
                    ? a.getDataPrevistaDevolucao() : a.getDataDevolucao();
            if (dataRef == null) continue;

            String nomeCliente = a.getCliente() != null ? a.getCliente().getNome() : "—";

            if (dataRef.isBefore(hoje)) {
                long dias = java.time.temporal.ChronoUnit.DAYS.between(dataRef, hoje);
                itens.add("🔴 " + nomeCliente + " — " + dias + " dia(s) em atraso");
            } else if (!dataRef.isAfter(limite)) {
                if (dataRef.isEqual(hoje)) {
                    itens.add("⚠ " + nomeCliente + " — vence hoje (" + dataRef.format(FMT) + ")");
                } else {
                    long dias = java.time.temporal.ChronoUnit.DAYS.between(hoje, dataRef);
                    itens.add("⚠ " + nomeCliente + " — vence em " + dias + " dia(s) (" + dataRef.format(FMT) + ")");
                }
            }
        }

        if (itens.isEmpty()) itens.add("✅ Nenhum aluguel expirando nos próximos 7 dias.");
        listaAlugueisExpirando.setItems(FXCollections.observableArrayList(itens));
    }

    private void carregarRenovacoes() {
        List<String> itens = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        for (Aluguel a : facade.listarAlugueis()) {
            if (!"Ativo".equals(a.getStatus())) continue;
            if (a.getDataPrevistaDevolucao() == null) continue;

            long diasParaVencer = java.time.temporal.ChronoUnit.DAYS
                    .between(hoje, a.getDataPrevistaDevolucao());

            if (diasParaVencer >= 0 && diasParaVencer <= 2 && !a.isRenovado()) {
                String nomeCliente = a.getCliente() != null ? a.getCliente().getNome() : "—";
                LocalDate novaData = a.getDataPrevistaDevolucao().plusDays(7);
                String produto = a.getProdutos().isEmpty() ? "item"
                        : a.getProdutos().get(0).getTitulo();
                itens.add("🔄 " + nomeCliente + " — \"" + produto
                        + "\" → Nova data: " + novaData.format(FMT));
            }
        }

        if (itens.isEmpty()) itens.add("✅ Nenhuma renovação automática pendente.");
        listaRenovacoes.setItems(FXCollections.observableArrayList(itens));
    }

    private String mesEmPortugues(int mes) {
        String[] meses = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        return meses[mes - 1];
    }

    @FXML public void abrirLivros()       { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLivros.fxml",        "Duduteca - Livros"); }
    @FXML public void abrirDiscos()       { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDiscos.fxml",        "Duduteca - Discos"); }
    @FXML public void abrirClientes()     { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaClientes.fxml",      "Duduteca - Clientes"); }
    @FXML public void abrirAlugueis()     { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaAlugueis.fxml",      "Duduteca - Aluguéis"); }
    @FXML public void abrirRelatorio()    { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaRelatorio.fxml",     "Duduteca - Relatório"); }
    @FXML public void abrirDashboard()    { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml",     "Duduteca - Dashboard"); }
    @FXML public void abrirFuncionarios() {
        if (!SessaoUsuario.isGerente()) return; // proteção extra
        navegar("/br/edu/ufersa/LEVI/view/fxml/TelaFuncionarios.fxml", "Duduteca - Funcionários");
    }
    @FXML public void handleSair() {
        SessaoUsuario.encerrarSessao();
        navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLogin.fxml", "Duduteca - Login");
    }

    private void navegar(String fxml, String titulo) {
        try { App.trocarTela(fxml, titulo); }
        catch (IOException e) { System.err.println("Erro ao navegar: " + e.getMessage()); }
    }
}
