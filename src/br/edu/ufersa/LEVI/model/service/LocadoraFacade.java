package br.edu.ufersa.LEVI.model.service;

import br.edu.ufersa.LEVI.model.entity.Aluguel;
import br.edu.ufersa.LEVI.model.entity.Cliente;
import br.edu.ufersa.LEVI.model.entity.Disco;
import br.edu.ufersa.LEVI.model.entity.Funcionarios;
import br.edu.ufersa.LEVI.model.entity.Livro;

import java.time.LocalDate;
import java.util.List;

// Implementa o padrão Facade: dá às telas (Controllers) UM ÚNICO PONTO DE
// ACESSO a todas as operações do sistema, em vez de cada Controller precisar
// conhecer e instanciar 5 Services diferentes (ClienteService, LivroService,
// DiscoService, AluguelService, FuncionariosService).
//
// A Facade não tem regra de negócio própria — ela só repassa ("delega") cada
// chamada para o Service responsável. Quem decide as regras continua sendo
// cada Service individual.
public class LocadoraFacade {

    private final ClienteService clienteService = new ClienteService();
    private final LivroService livroService = new LivroService();
    private final DiscoService discoService = new DiscoService();
    private final AluguelService aluguelService = new AluguelService();
    private final FuncionariosService funcionariosService = new FuncionariosService();

    // ===================== CLIENTE =====================

    public void salvarCliente(Cliente cliente) throws Exception {
        clienteService.salvarCliente(cliente);
    }

    public void atualizarCliente(Cliente cliente) throws Exception {
        clienteService.atualizarCliente(cliente);
    }

    public void excluirCliente(Cliente cliente) throws Exception {
        clienteService.excluirCliente(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteService.listarClientes();
    }

    public List<Cliente> pesquisarClientes(String termo) {
        return clienteService.pesquisarClientes(termo);
    }

    // ===================== LIVRO =====================

    public void salvarLivro(Livro livro) throws Exception {
        livroService.salvarLivro(livro);
    }

    public void atualizarLivro(Livro livro) throws Exception {
        livroService.atualizarLivro(livro);
    }

    public void excluirLivro(Livro livro) throws Exception {
        livroService.excluirLivro(livro);
    }

    public List<Livro> listarLivros() {
        return livroService.listarLivros();
    }

    public List<Livro> pesquisarLivros(String termo) {
        return livroService.pesquisarLivros(termo);
    }

    // ===================== DISCO =====================

    public void salvarDisco(Disco disco) throws Exception {
        discoService.salvarDisco(disco);
    }

    public void atualizarDisco(Disco disco) throws Exception {
        discoService.atualizarDisco(disco);
    }

    public void excluirDisco(Disco disco) throws Exception {
        discoService.excluirDisco(disco);
    }

    public List<Disco> listarDiscos() {
        return discoService.listarDiscos();
    }

    public List<Disco> pesquisarDiscos(String termo) {
        return discoService.pesquisarDiscos(termo);
    }

    // ===================== ALUGUEL =====================

    public Aluguel registrarAluguel(Aluguel aluguel) {
        return aluguelService.registrar(aluguel);
    }

    public Aluguel finalizarAluguel(Aluguel aluguel, LocalDate dataDevolucao) {
        return aluguelService.finalizar(aluguel, dataDevolucao);
    }

    public Aluguel excluirAluguel(Aluguel aluguel) {
        return aluguelService.deletar(aluguel);
    }

    public List<Aluguel> buscarAlugueis(String parametro) {
        return aluguelService.buscar(parametro);
    }

    public List<Aluguel> listarAlugueis() {
        return aluguelService.listar();
    }

    public List<Aluguel> buscarAlugueisPorMes(int mes, int ano) {
        return aluguelService.buscarPorMes(mes, ano);
    }

    public List<Aluguel> buscarAlugueisPorCliente(Cliente cliente) {
        return aluguelService.buscarPorCliente(cliente);
    }

    public List<Aluguel> processarRenovacoesAutomaticas() {
        return aluguelService.processarRenovacoesAutomaticas();
    }

    public float calcularFaturamentoMes(int mes, int ano) {
        return aluguelService.calcularFaturamentoMes(mes, ano);
    }

    // ===================== FUNCIONARIO =====================

    public Funcionarios cadastrarFuncionario(Funcionarios funcionario) {
        return funcionariosService.inserir(funcionario);
    }

    public Funcionarios atualizarFuncionario(Funcionarios funcionario) {
        return funcionariosService.alterar(funcionario);
    }

    public Funcionarios excluirFuncionario(Funcionarios funcionario) {
        return funcionariosService.deletar(funcionario);
    }

    public List<Funcionarios> buscarFuncionarios(String parametro) {
        return funcionariosService.buscar(parametro);
    }

    public List<Funcionarios> listarFuncionarios() {
        return funcionariosService.listar();
    }

    public Funcionarios autenticar(String email, String senha) {
        return funcionariosService.autenticar(email, senha);
    }
}
