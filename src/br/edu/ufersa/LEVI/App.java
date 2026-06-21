package br.edu.ufersa.LEVI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws Exception {
        stagePrincipal = stage;
        trocarTela("/br/edu/ufersa/LEVI/view/fxml/TelaLogin.fxml", "Duduteca - Login");
    }

    // Carrega um novo FXML e troca a Scene da Stage principal.
    // Qualquer controller pode chamar isso para navegar entre telas.
    public static void trocarTela(String caminhoFxml, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(caminhoFxml));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stagePrincipal.setScene(scene);
        stagePrincipal.setTitle(titulo);
        stagePrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}