package org.example.matchinggameclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải file FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(loader.load());

        // Thiết lập tiêu đề cho cửa sổ
        primaryStage.setTitle("Memory Game");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/org/example/matchinggameclient/img/logo.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
