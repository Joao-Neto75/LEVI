module br.edu.ufersa.LEVI {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens br.edu.ufersa.LEVI to javafx.fxml, javafx.graphics;
    opens br.edu.ufersa.LEVI.view.Controller to javafx.fxml;

    exports br.edu.ufersa.LEVI;
    exports br.edu.ufersa.LEVI.view.Controller;
}