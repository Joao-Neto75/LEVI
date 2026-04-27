package br.edu.ufersa.LEVI.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sistema {
    private List<Cliente> clientes;
    private List<Disco> discos;
    private List<Livro> livros;
    private List<Aluguel> alugueis;

    public Sistema() {
        this.clientes = new ArrayList<>();
        this.discos = new ArrayList<>();
        this.livros = new ArrayList<>();
        this.alugueis = new ArrayList<>();
    }

    public void cadastrarCliente(Cliente c) {
        if (c == null){
            System.out.println("ERRO: Cliente invalido.");
        return;
        }
        for (Cliente existente : clientes) {
            if (existente.getCpf().equals(c.getCpf())) {
                System.out.println("ERRO: Já existe um cliente com este CPF.");
                return;
            }
        }
        this.clientes.add(c);
        System.out.println("Cliente " + c.getNome() + " cadastrado com sucesso!");
    }

    public void alterarCliente(String cpf, Cliente novoCliente) {
        for (int i = 0; i < clientes.size(); i++) {
            Cliente clienteExistente = clientes.get(i);
            if (clienteExistente.getCpf().equals(cpf)) {
                clientes.set(i, novoCliente);
                System.out.println("Dados do cliente com CPF " + cpf + " atualizados com sucesso!");
                return;
            }
        }
        System.out.println("ERRO: Cliente com CPF " + cpf + " não encontrado!");
    }

    public void excluirCliente(String cpf) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getCpf().equals(cpf)) {
                clientes.remove(i);
                System.out.println("Cliente com CPF " + cpf + " removido com sucesso!");
                return;
            }
        }
        System.out.println("ERRO: Não foi possível excluir. CPF " + cpf + " não encontrado.");
    }

    public void cadastrarLivro(Livro l) {
        if (l != null) {
            this.livros.add(l);
            System.out.println("Livro " + l.getTitulo() + " cadastrado!");
        }
    }

    public void alterarLivro(String titulo, Livro novoLivro) {
        for (int i = 0; i < livros.size(); i++) {
            if (livros.get(i).getTitulo().equalsIgnoreCase(titulo)) {
                livros.set(i, novoLivro);
                System.out.println("Livro " + titulo + " atualizado!");
                return;
            }
        }
        System.out.println("Livro não encontrado.");
    }

    public void excluirLivro(String titulo) {
        for (int i = 0; i < livros.size(); i++) {
            if (livros.get(i).getTitulo().equalsIgnoreCase(titulo)) {
                livros.remove(i);
                System.out.println("Livro removido!");
                return;
            }
        }
        System.out.println("Não foi possível excluir o livro.");
    }

    public void cadastrarDisco(Disco d) {
        if (d != null) {
            this.discos.add(d);
            System.out.println("Disco " + d.getTitulo() + " cadastrado com sucesso!");
        }
    }

    public void alterarDisco(String titulo, Disco novoDisco) {
        for (int i = 0; i < discos.size(); i++) {
            // Usamos equalsIgnoreCase para facilitar a busca pelo usuário
            if (discos.get(i).getTitulo().equalsIgnoreCase(titulo)) {
                discos.set(i, novoDisco);
                System.out.println("Disco " + titulo + " atualizado com sucesso!");
                return;
            }
        }
        System.out.println("ERRO: Disco com o título " + titulo + " não encontrado.");
    }

    public void excluirDisco(String titulo) {
        for (int i = 0; i < discos.size(); i++) {
            if (discos.get(i).getTitulo().equalsIgnoreCase(titulo)) {
                discos.remove(i);
                System.out.println("Disco " + titulo + " removido do sistema!");
                return;
            }
        }
        System.out.println("ERRO: Não foi possível localizar o disco " + titulo + " para exclusão.");
    }

    public List<Livro> pesquisarLivroPorTitulo(String titulo) {
        List<Livro> resultados = new ArrayList<>();
        for (Livro l : livros) {
            if (l.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                resultados.add(l);
            }
        }
        return resultados;
    }

    public List<Livro> pesquisarLivroPorGenero(String genero) {
        List<Livro> resultados = new ArrayList<>();
        for (Livro l : livros) {
            if (l.getGenero().equalsIgnoreCase(genero)) {
                resultados.add(l);
            }
        }
        return resultados;
    }

}
