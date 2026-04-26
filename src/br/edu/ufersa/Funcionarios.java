package br.edu.ufersa.LEVI.model.entity;
import java.util.Date;

public class Funcionarios {
    private String nome;
    private String cargo;
    private double salario;
    private Date contratacao;

    // Construtor
    public Funcionarios(String nome, String cargo, double salario, Date contratacao) {
        setNome(nome);
        setCargo(cargo);
        setSalario(salario);
        setContratacao(contratacao);
        
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getCargo() {
        return cargo;
    }
    
    public double getSalario() {
        return salario;
    }

    public Date getContratacao() {
        return contratacao;
    }

    // Setters
    public void setNome(String nome) {
        if (!nome.isEmpty()){
        this.nome = nome;}
        else {this.nome = "Sem nome";}
    }

    public void setCargo(String cargo) {
        if (!cargo.isEmpty()){
        this.cargo = cargo;}
        else {this.cargo = "Funcionario avulso";}
    }

    public void setSalario(double salario) {
        if (salario > 0){
        this.salario = salario;}
        else {this.salario = 0.0;}
    }

    public void setContratacao(Date contratacao) {
        if (contratacao != null){
        this.contratacao = contratacao;}
        else {this.contratacao = new Date();}
    }

    // Métodos de atualização
    public void atualizarCargo(String novoCargo) {
        setCargo(novoCargo);
    } 

    public void atualizarSalario(double novoSalario) {
        setSalario(novoSalario);
    }

    public void exibirInformacoes() {
        System.out.println("Nome: " + getNome());
        System.out.println("Cargo: " + getCargo());
        System.out.printf("Salário: R$ %.2f\n" + getSalario());
        System.out.println("Data de Contratação: " + getContratacao());
    }
    
}
