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
        carregarVisaoGeral();
        carregarMaisBuscados();
        carregarAlugueisExpirando();
        carregarRenovacoes();
    }

    private void exibirFuncionarioLogado() {
        Funcionarios logado = SessaoUsuario.getFuncionarioLogado();
        if (logado != null) {

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
                .filter(a -> a.getDataDevolucao() == null)
                .count();
        labelAlugueisAtivos.setText("Aluguéis Ativos: " + alugueisAtivos);

        labelVisaoGeralTitulo.setText("Visão Geral (" + mesEmPortugues(hoje.getMonthValue()) + "/" + hoje.getYear() + ")");
    }

    private void carregarMaisBuscados() {
        List<String> itens = new ArrayList<>();

        List<Livro> livros = facade.listarLivros();
        livros.stream()
                .sorted((a, b) -> Integer.compare(b.getExemplares(), a.getExemplares()))
                .limit(3)
                .forEach(l -> itens.add("📖 " + l.getTitulo() + " — " + l.getAutor()));

        List<Disco> discos = facade.listarDiscos();
        discos.stream()
                .sorted((a, b) -> Integer.compare(b.getExemplares(), a.getExemplares()))
                .limit(2)
                .forEach(d -> itens.add("🎵 " + d.getTitulo() + " — " + d.getBanda()));

        if (itens.isEmpty()) itens.add("Nenhum item cadastrado ainda.");
        listaMaisBuscados.setItems(FXCollections.observableArrayList(itens));
    }

    private void carregarAlugueisExpirando() {
        List<String> itens = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);

        List<Aluguel> alugueis = facade.listarAlugueis();
        for (Aluguel a : alugueis) {
            if (a.getDataDevolucao() == null) continue;
            LocalDate dev = a.getDataDevolucao();
            if (!dev.isBefore(hoje) && !dev.isAfter(limite)) {
                String nomeCliente = a.getCliente() != null ? a.getCliente().getNome() : "—";
                itens.add("⚠ " + nomeCliente + " — devolução: " + dev.format(FMT));
            }
        }

        // Também mostra em aberto há mais de 7 dias
        for (Aluguel a : alugueis) {
            if (a.getDataDevolucao() != null) continue;
            LocalDate emp = a.getDataEmprestimo();
            if (emp != null && emp.plusDays(7).isBefore(hoje)) {
                String nomeCliente = a.getCliente() != null ? a.getCliente().getNome() : "—";
                itens.add("🔴 " + nomeCliente + " — desde: " + emp.format(FMT));
            }
        }

        if (itens.isEmpty()) itens.add("✅ Nenhum aluguel expirando nos próximos 7 dias.");
        listaAlugueisExpirando.setItems(FXCollections.observableArrayList(itens));
    }

    private void carregarRenovacoes() {
        List<String> itens = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        List<Aluguel> alugueis = facade.listarAlugueis();
        for (Aluguel a : alugueis) {
            if (a.getDataDevolucao() == null) continue;
            if (a.getDataDevolucao().isBefore(hoje)) {
                String nomeCliente = a.getCliente() != null ? a.getCliente().getNome() : "—";
                long dias = java.time.temporal.ChronoUnit.DAYS.between(a.getDataDevolucao(), hoje);
                itens.add("❗ " + nomeCliente + " — " + dias + " dia(s) em atraso");
            }
        }

        if (itens.isEmpty()) itens.add("✅ Nenhum aluguel em atraso.");
        listaRenovacoes.setItems(FXCollections.observableArrayList(itens));
    }

    private String mesEmPortugues(int mes) {
        String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        return meses[mes - 1];
    }

    @FXML public void abrirLivros() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLivros.fxml", "Duduteca - Livros"); }
    @FXML public void abrirDiscos() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDiscos.fxml", "Duduteca - Discos"); }
    @FXML public void abrirClientes() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaClientes.fxml", "Duduteca - Clientes"); }
    @FXML public void abrirAlugueis() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaAlugueis.fxml", "Duduteca - Aluguéis"); }
    @FXML public void abrirRelatorio() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaRelatorio.fxml", "Duduteca - Relatório"); }
    @FXML public void abrirDashboard() { navegar("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml", "Duduteca - Dashboard"); }
    @FXML public void handleSair() { SessaoUsuario.encerrarSessao(); navegar("/br/edu/ufersa/LEVI/view/fxml/TelaLogin.fxml", "Duduteca - Login"); }

    private void navegar(String fxml, String titulo) {
        try { App.trocarTela(fxml, titulo); }
        catch (IOException e) { labelFuncionarioLogado.setText("Erro: " + e.getMessage()); }
    }
}