package br.edu.ufersa.LEVI.model.service;



public class FuncionariosService {
    BaseDao<Funcionarios> dao = new FuncionariosDao();
    
    public Funcionarios inseir (Funcionarios entity){
        Funcionarios f = dao.inserir(entity);
        
    }





}