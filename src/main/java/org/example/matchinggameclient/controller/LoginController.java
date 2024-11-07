package org.example.matchinggameclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import org.example.matchinggameclient.model.Card;
import org.example.matchinggameclient.model.Invitation;
import org.example.matchinggameclient.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML
    private TextField usernametextField; // Reference to the username input field

    @FXML
    private TextField passwordtextField; // Reference to the password input field

    @FXML
    private Text signupLabel; // Reference to the 'Đăng ký ngay' Text node

    @FXML
    private Text statusText; // Text node for displaying status messages

    @FXML
    private Button loginBtn; // Reference to the login button

    private SocketHandle socketHandle; // Instance of SocketHandle

    private User client; // Current logged-in user
    private ArrayList<User> userList = new ArrayList<>(); // List of users

    @FXML
    public void initialize() {
        // Initialize the SocketHandle instance
        socketHandle = SocketHandle.getInstance();
        socketHandle.setLoginController(this); // Set the LoginController instance
        statusText.setText(""); // Clear status text

        // Clear statusText when the user types in the username or password fields
        usernametextField.setOnKeyReleased(event -> clearStatusText());
        passwordtextField.setOnKeyReleased(event -> clearStatusText());
    }

    @FXML
    private void handleLoginBtn(ActionEvent event) {
        String username = usernametextField.getText().trim(); // Get trimmed username
        String password = passwordtextField.getText().trim(); // Get trimmed password

        // Validate that both fields are filled
        if (username.isEmpty() || password.isEmpty()) {
            statusText.setText("Both fields are required!"); // Set status text for empty fields
            return; // Exit the method if validation fails
        }

        // Create request and send it
        String request = "client-verify," + username + "," + password;
        try {
            socketHandle.write(request); // Send login request
            socketHandle.request = "login";
        } catch (IOException e) {
            e.printStackTrace(); // Handle IOException
        }
    }

    private void clearStatusText() {
        statusText.setText(""); // Clear the status text
    }

    @FXML
    private void handleSignupLabel(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/Signup.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) signupLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show(); // Show signup scene
        } catch (IOException e) {
            e.printStackTrace(); // Handle IOException
        }
    }

    public void loginToHome(int clientId, ArrayList<Invitation> invitationList, ArrayList<User> playerList, String chatServerContent) {
        Platform.runLater(() -> { // Chạy trong luồng FX
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/Home.fxml"));
                Parent root = loader.load();
                HomeController homeController = loader.getController();

                homeController.loadData(clientId, invitationList, playerList, chatServerContent);

                // Cập nhật giai điệu và ẩn cảnh hiện tại
                Stage stage = (Stage) usernametextField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Memory Game");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Xử lý IOException
            }
        });
    }
//    public void loginToGame(Long matchId, Integer opponentId, String opponentUsername, List<Card> cards){
//
//        Platform.runLater(() -> { // Chạy trong luồng FX
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/game_view.fxml"));
//                Parent root = loader.load();
//                GameController gameController = loader.getController();
//
//                gameController.loadData(client, matchId, opponentId, opponentUsername, cards);
//
//                // Cập nhật giai điệu và ẩn cảnh hiện tại
////                Stage stage = (Stage) searchTextField.getScene().getWindow();
////                stage.setScene(new Scene(root));
////                stage.setTitle("Memory Game");
////                stage.show();
//            } catch (IOException e) {
//                e.printStackTrace(); // Xử lý IOException
//            }
//        });
//    }



    public void setTextWrongUser() {
        statusText.setText("Invalid username or password!"); // Show invalid login message
    }

    public void loginSuccess() {
        statusText.setText("Login successful!"); // Show successful login message
    }

    public void setDuplicateLogin() {
        statusText.setText("User is already logged in."); // Show duplicate login message
    }
}
