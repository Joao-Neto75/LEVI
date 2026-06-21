package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.model.entity.Funcionarios;

// Guarda o funcionário que fez login enquanto o programa estiver aberto.
// Qualquer controller pode chamar SessaoUsuario.getFuncionarioLogado()
// para saber quem está usando o sistema no momento.
public class SessaoUsuario {

    private static Funcionarios funcionarioLogado;

    private SessaoUsuario() {
        // classe utilitária, não deve ser instanciada
    }

    public static void setFuncionarioLogado(Funcionarios funcionario) {
        funcionarioLogado = funcionario;
    }

    public static Funcionarios getFuncionarioLogado() {
        return funcionarioLogado;
    }

    public static void encerrarSessao() {
        funcionarioLogado = null;
    }
}