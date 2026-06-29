package br.edu.ufersa.LEVI.model.service;
import br.edu.ufersa.LEVI.model.dao.FuncionariosDao;
import br.edu.ufersa.LEVI.model.dao.BaseDao;
import br.edu.ufersa.LEVI.model.entity.Funcionarios;
import br.edu.ufersa.LEVI.model.exception.AutenticacaoException;
import java.util.List;


public class FuncionariosService {
    BaseDao<Funcionarios> dao = new FuncionariosDao();
    FuncionariosDao funcionariosDao = new FuncionariosDao();

    public Funcionarios inserir (Funcionarios entity){
        Funcionarios f = dao.inserir(entity);
        return f;
    }

    public Funcionarios deletar (Funcionarios entity){
        Funcionarios f = dao.deletar(entity);
        return f;
    }

    public Funcionarios alterar (Funcionarios entity){
        Funcionarios f = dao.alterar(entity);
        return f;
    }

    public List<Funcionarios> buscar(String parametro) {
        return dao.buscar(parametro);
    }

    public List<Funcionarios> listar() {
        return dao.listar();
    }

    // Usado pela tela de login: verifica e-mail e senha e devolve o funcionário autenticado
    public Funcionarios autenticar(String email, String senha) {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            throw new AutenticacaoException("Informe e-mail e senha!");
        }

        Funcionarios funcionario = funcionariosDao.buscarPorEmail(email);

        if (funcionario == null) {
            throw new AutenticacaoException("E-mail ou senha inválidos!");
        }

        if (!funcionario.autenticar(email, senha)) {
            throw new AutenticacaoException("E-mail ou senha inválidos!");
        }

        return funcionario;
    }

}