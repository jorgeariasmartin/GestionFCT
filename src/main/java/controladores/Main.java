package controladores;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/vistas/main-view.fxml"));
            Pane ventana = (Pane) loader.load();
            Scene scene = new Scene(ventana);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}