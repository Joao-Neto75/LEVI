package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.App;
import br.edu.ufersa.LEVI.model.entity.Funcionarios;
import br.edu.ufersa.LEVI.model.service.LocadoraFacade;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDate;

public class DashboardController {

    // Sidebar
    @FXML private Button botaoLivros;
    @FXML private Button botaoDiscos;
    @FXML private Button botaoClientes;
    @FXML private Button botaoAlugueis;
    @FXML private Button botaoSair;

    // Topo
    @FXML private TextField campoBusca;
    @FXML private Label labelFuncionarioLogado;
    @FXML private Button botaoRelatorio;

    // Cards
    @FXML private ListView<String> listaMaisBuscados;
    @FXML private ListView<String> listaAlugueisExpirando;
    @FXML private ListView<String> listaRenovacoes;
    @FXML private Label labelVisaoGeralTitulo;
    @FXML private Label labelFaturamento;
    @FXML private Label labelAlugueisAtivos;
    @FXML private Label labelEstoque;

    // Antes: três Services instanciados separadamente
    // (AluguelService, LivroService, DiscoService).
    // Agora: uma única Facade cobre os três.
    private final LocadoraFacade facade = new LocadoraFacade();

    @FXML
    public void initialize() {
        exibirFuncionarioLogado();
        carregarVisaoGeral();
        // listaMaisBuscados, listaAlugueisExpirando e listaRenovacoes dependem
        // de regras que o grupo ainda vai definir (ex: o que conta como "mais buscado"),
        // por isso ficam vazias por enquanto.
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
        int totalEstoque = totalLivros + totalDiscos;
        labelEstoque.setText(String.format("Total em Estoque: %d (%d livros, %d discos)",
                totalEstoque, totalLivros, totalDiscos));

        int alugueisAtivos = facade.listarAlugueis().size();
        labelAlugueisAtivos.setText("Aluguéis Ativos: " + alugueisAtivos);

        labelVisaoGeralTitulo.setText("Visão Geral (" + mesEmPortugues(hoje.getMonthValue()) + "/" + hoje.getYear() + ")");
    }

    private String mesEmPortugues(int mes) {
        String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        return meses[mes - 1];
    }

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
        trocarTelaComTratamento("/br/edu/ufersa/LEVI/view/fxml/TelaRelatorio.fxml", "Duduteca - Relatório");
    }

    @FXML
    public void handleSair() {
        SessaoUsuario.encerrarSessao();
        trocarTelaComTratamento("/br/edu/ufersa/LEVI/view/fxml/TelaLogin.fxml", "Duduteca - Login");
    }

    @FXML
    public void abrirDashboard() {
        trocarTelaComTratamento("/br/edu/ufersa/LEVI/view/fxml/TelaDashboard.fxml", "Duduteca - Dashboard");
    }

    private void trocarTelaComTratamento(String caminhoFxml, String titulo) {
        try {
            App.trocarTela(caminhoFxml, titulo);
        } catch (IOException e) {
            // Essas telas ainda não existem; quando forem criadas, a navegação já funciona.
            labelFuncionarioLogado.setText("Tela ainda não implementada: " + caminhoFxml);
            // TEMPORÁRIO: imprime o stack trace completo no console para diagnosticar
            e.printStackTrace();
        }
    }

}
