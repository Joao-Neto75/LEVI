package br.edu.ufersa.LEVI.service;

import br.edu.ufersa.LEVI.model.dao.ClienteDao;
import br.edu.ufersa.LEVI.model.entity.Cliente;
import java.util.List;

public class ClienteService {

    private final ClienteDao clienteDao;

    public ClienteService() {
        this.clienteDao = new ClienteDao();
    }

    public void salvarCliente(Cliente cliente) throws Exception {
        if (cliente.getNome().equalsIgnoreCase("Fantasma") || cliente.getNome().trim().isEmpty()) {
            throw new Exception("Não é possível cadastrar um cliente sem um nome válido.");
        }

        if (cliente.getCpf().equalsIgnoreCase("Não existe") || cliente.getCpf().trim().isEmpty()) {
            throw new Exception("O CPF informado é inválido ou está em branco.");
        }

        clienteDao.inserir(cliente);
    }

    public void atualizarCliente(Cliente cliente) throws Exception {
        if (cliente.getId() <= 0) {
            throw new Exception("Para atualizar, o cliente precisa ter um ID válido.");
        }
        clienteDao.alterar(cliente);
    }

    public void excluirCliente(Cliente cliente) throws Exception {
        if (cliente.getId() <= 0) {
            throw new Exception("Para excluir, o cliente precisa ter um ID válido.");
        }
        clienteDao.deletar(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteDao.listar();
    }

    public List<Cliente> pesquisarClientes(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return clienteDao.listar();
        }
        return clienteDao.buscar(termo);
    }
}