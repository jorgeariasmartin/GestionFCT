module controllers.gestionfct {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens controladores to javafx.fxml;
    exports controladores;

    opens modelos to javafx.base;
}