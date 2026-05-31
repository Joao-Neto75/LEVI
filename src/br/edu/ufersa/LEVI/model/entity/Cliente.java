package br.edu.ufersa.LEVI.model.entity;

public class Cliente {

    private String cpf;
    private int id;
    private String nome;
    private String endereco;

    public Cliente() {
        this.nome = "Sem nome";
        this.endereco = "Sem endereço";
        this.cpf = "000.000.000-00";
    }

    public Cliente(String nome, String endereco, String cpf) {
        setCpf(cpf);
        setNome(nome);
        setEndereco(endereco);
    }

    public void atualizarEndereco(String novoEndereco) {
        setEndereco(novoEndereco);
    }

    public void atualizarNome(String novoNome) {
        setNome(novoNome);
    }

    // Getters
    public String getNome() { return nome; }
    public int getId() { return id; }
    public String getEndereco() { return endereco; }
    public String getCpf() { return cpf; }

    // Setters com validação
    public void setNome(String nome) {
        if (!nome.isEmpty()){
        this.nome = nome;}
        else {this.nome = "Fantasma";}
    }

    public void setId(int id) { this.id = id; }

    public void setEndereco(String endereco) {
        if (!endereco.isEmpty()){
        this.endereco = endereco;}
        else { this.endereco = "Sem endereço";}
    }

    public void setCpf(String cpf) {
        if (!cpf.isEmpty()){
        this.cpf = cpf;}
        else {this.cpf = "Não existe";}
    }


}
