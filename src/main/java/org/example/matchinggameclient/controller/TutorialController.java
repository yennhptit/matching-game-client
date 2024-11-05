package org.example.matchinggameclient.controller;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.matchinggameclient.model.Invitation;
import org.example.matchinggameclient.model.User;

import java.io.IOException;
import java.util.ArrayList;

public class TutorialController {
    @FXML
    private ImageView homeBtn; // This should match your FXML id
    private SocketHandle socketHandle;
    @FXML
    private Pane pane1;

    @FXML
    private Pane pane2;

    @FXML
    private ImageView nextBtn; // Assuming you have an ImageView for the Next button

    @FXML
    private ImageView backBtn; // Assuming you have an ImageView for the Back button
    @FXML
    private void initialize() {
        socketHandle = SocketHandle.getInstance(); // Pass the current controller as the message handler
        // Set up the event handler for the home button
        homeBtn.setOnMouseClicked(event -> {
            playHomeButtonAnimation(); // Chạy hiệu ứng khi nhấn nút home
            goToHome(); // Gọi phương thức goToHome
        });

        pane1.setVisible(true);
        pane2.setVisible(false);

        backBtn.setVisible(false); // Hide back button initially

        // Set action for the Next button
        nextBtn.setOnMouseClicked(event -> showPane2());

        // Set action for the Back button
        backBtn.setOnMouseClicked(event -> showPane1());
    }

    public void goToHome() {
        Platform.runLater(() -> { // Chạy trong luồng FX
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/Home.fxml"));
                Parent root = loader.load();
                HomeController homeController = loader.getController();

                int clientId = socketHandle.client.getID();
                ArrayList<Invitation> invitationList = socketHandle.invitations;
                ArrayList<User> playerList = socketHandle.userList;

                homeController.loadData(clientId, invitationList, playerList, "");

                // Cập nhật giai điệu và ẩn cảnh hiện tại
                Stage stage = (Stage) homeBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Memory Game");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Xử lý IOException
            }
        });
    }
    private void playHomeButtonAnimation() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), homeBtn);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2); // Phóng to 20%
        scaleTransition.setToY(1.2);
        scaleTransition.setAutoReverse(true); // Đảo ngược hiệu ứng
        scaleTransition.setCycleCount(2); // Thực hiện hai lần (phóng to và thu nhỏ)
        scaleTransition.play(); // Chạy hiệu ứng
    }
    private void showPane2() {
        pane1.setVisible(false);
        pane2.setVisible(true);
        nextBtn.setVisible(false); // Hide back button initially
        backBtn.setVisible(true); // Show back button
    }

    private void showPane1() {
        pane1.setVisible(true);
        pane2.setVisible(false);
        nextBtn.setVisible(true); // Show back button initially
        backBtn.setVisible(false); // Hide back button

    }
}
