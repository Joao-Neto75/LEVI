module LEVI {


    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens br.edu.ufersa.LEVI.view to javafx.fxml, javafx.graphics;



    exports br.edu.ufersa.LEVI.view;


}