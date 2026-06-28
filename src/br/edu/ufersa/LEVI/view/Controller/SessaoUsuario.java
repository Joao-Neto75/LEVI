package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.model.entity.Funcionarios;

public class SessaoUsuario {

    private static Funcionarios funcionarioLogado;

    private SessaoUsuario() {}

    public static void setFuncionarioLogado(Funcionarios funcionario) {
        funcionarioLogado = funcionario;
    }

    public static Funcionarios getFuncionarioLogado() {
        return funcionarioLogado;
    }

    public static void encerrarSessao() {
        funcionarioLogado = null;
    }

    // Retorna true se o funcionário logado for Gerente
    public static boolean isGerente() {
        return funcionarioLogado != null
            && "Gerente".equalsIgnoreCase(funcionarioLogado.getCargo());
    }
}
