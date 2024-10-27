package org.example.matchinggameclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải file FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/Test.fxml"));
        Scene scene = new Scene(loader.load());

        // Thiết lập tiêu đề cho cửa sổ
        primaryStage.setTitle("Memory Game");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/org/example/matchinggameclient/img/logo.png")));
        primaryStage.setScene(scene);


        // Ngăn không cho phép thay đổi kích thước cửa sổ
        primaryStage.setResizable(false);


        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
