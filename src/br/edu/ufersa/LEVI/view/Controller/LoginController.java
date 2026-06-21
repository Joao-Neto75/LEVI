package br.edu.ufersa.LEVI.view.Controller;

import br.edu.ufersa.LEVI.App;
import br.edu.ufersa.LEVI.model.entity.Funcionarios;
import br.edu.ufersa.LEVI.model.service.FuncionariosService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private Button botaoEntrar;
    @FXML private Label labelErro;

    private final FuncionariosService funcionariosService = new FuncionariosService();

    @FXML
    public void handleLogin() {
        labelErro.setText("");

        String email = campoEmail.getText();
        String senha = campoSenha.getText();

        try {
            Funcionarios logado = funcionariosService.autenticar(email, senha);

            // guarda o funcionário logado para uso nas próximas telas
            SessaoUsuario.setFuncionarioLogado(logado);

            App.trocarTela("/br/edu/ufersa/LEVI/view/fxml/dashboard.fxml", "Duduteca - Dashboard");

        } catch (RuntimeException e) {
            // mensagens como "E-mail ou senha inválidos!" vindas do FuncionariosService
            labelErro.setText(e.getMessage());
        } catch (IOException e) {
            labelErro.setText("Erro ao carregar a próxima tela. Avise o grupo!");
        }
    }
}