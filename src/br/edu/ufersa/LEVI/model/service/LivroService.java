package br.edu.ufersa.LEVI.service;

import br.edu.ufersa.LEVI.model.dao.LivroDao;
import br.edu.ufersa.LEVI.model.entity.Livro;
import java.util.List;

public class LivroService {

    private final LivroDao livroDao;

    public LivroService() {
        this.livroDao = new LivroDao();
    }

    public void salvarLivro(Livro livro) throws Exception {
        if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty() || livro.getTitulo().equalsIgnoreCase("Sem título")) {
            throw new Exception("O livro precisa ter um título válido para ser cadastrado.");
        }

        if (livro.getAutor().equalsIgnoreCase("Sem autor") || livro.getAutor().trim().isEmpty()) {
            throw new Exception("Não é possível cadastrar um livro sem o autor correspondente.");
        }

        if (livro.getExemplares() < 0) {
            throw new Exception("A quantidade de exemplares em estoque não pode ser negativa.");
        }

        livroDao.inserir(livro);
    }

    public void atualizarLivro(Livro livro) throws Exception {
        if (livro.getId() <= 0) {
            throw new Exception("ID inválido. Não foi possível atualizar o livro.");
        }
        livroDao.alterar(livro);
    }

    public void excluirLivro(Livro livro) throws Exception {
        if (livro.getId() <= 0) {
            throw new Exception("ID inválido. Não foi possível remover o livro.");
        }
        livroDao.deletar(livro);
    }

    public List<Livro> listarLivros() {
        return livroDao.listar();
    }

    public List<Livro> pesquisarLivros(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return livroDao.listar();
        }
        return livroDao.buscar(termo);
    }
}