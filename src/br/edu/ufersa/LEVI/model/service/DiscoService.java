package br.edu.ufersa.LEVI.service;

import br.edu.ufersa.LEVI.model.dao.DiscoDao;
import br.edu.ufersa.LEVI.model.entity.Disco;
import java.util.List;

public class DiscoService {

    private final DiscoDao discoDao;

    public DiscoService() {
        this.discoDao = new DiscoDao();
    }

    public void salvarDisco(Disco disco) throws Exception {
        if (disco.getTitulo() == null || disco.getTitulo().trim().isEmpty() || disco.getTitulo().equalsIgnoreCase("Sem título")) {
            throw new Exception("O disco precisa ter um título válido para ser cadastrado.");
        }

        if (disco.getBanda().equalsIgnoreCase("Sem banda") || disco.getBanda().trim().isEmpty()) {
            throw new Exception("Não é possível cadastrar um disco sem informar a banda/artista.");
        }

        if (disco.getExemplares() < 0) {
            throw new Exception("A quantidade de exemplares em estoque não pode ser negativa.");
        }

        discoDao.inserir(disco);
    }

    public void atualizarDisco(Disco disco) throws Exception {
        if (disco.getId() <= 0) {
            throw new Exception("ID inválido. Não foi possível atualizar o disco.");
        }
        discoDao.alterar(disco);
    }

    public void excluirDisco(Disco disco) throws Exception {
        if (disco.getId() <= 0) {
            throw new Exception("ID inválido. Não foi possível remover o disco.");
        }
        discoDao.deletar(disco);
    }

    public List<Disco> listarDiscos() {
        return discoDao.listar();
    }

    public List<Disco> pesquisarDiscos(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return discoDao.listar();
        }
        return discoDao.buscar(termo);
    }
}