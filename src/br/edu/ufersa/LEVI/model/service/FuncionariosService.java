package br.edu.ufersa.LEVI.model.service;
import br.edu.ufersa.LEVI.model.dao.FuncionariosDao;
import br.edu.ufersa.LEVI.model.dao.BaseDao;
import br.edu.ufersa.LEVI.model.entity.Funcionarios;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class FuncionariosService {
    BaseDao<Funcionarios> dao = new FuncionariosDao();
    
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





}