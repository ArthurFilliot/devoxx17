package com.trytogetin.main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    private int intClose = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainframe.fxml"));
        primaryStage.setTitle("Try to get in");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (intClose++ < 3) {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Nice try");
                    alert.setHeaderText("Alt + F4 detected");
                    alert.setContentText("You know better than that");

                    alert.showAndWait();

                    event.consume();
                    primaryStage.show();
                }
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
