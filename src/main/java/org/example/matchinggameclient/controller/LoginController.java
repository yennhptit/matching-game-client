package org.example.matchinggameclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;

import java.io.IOException;

public class LoginController implements MessageHandler {

    @FXML
    private TextField usernametextField; // Reference to the username input field

    @FXML
    private TextField passwordtextField; // Reference to the password input field

    @FXML
    private Text signupLabel; // Reference to the 'Đăng ký ngay' Text node

    @FXML
    private Text statusText;

    private SocketHandle socketHandle; // Instance of SocketHandle

    public LoginController() {
        // SocketHandle will be initialized in the controller
    }

    @FXML
    public void initialize() {
        socketHandle = new SocketHandle(this); // Pass the current controller as the message handler
        new Thread(socketHandle).start(); // Start the socket communication in a separate thread
        statusText.setText("");

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

        // If validation passes, create request and send it
        String request = "client-verify," + username + "," + password;
        try {
            socketHandle.write(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(String message) {
        Platform.runLater(() -> {
            String[] messageSplit = message.split(",");
            if (messageSplit[0].equals("wrong-user")) {
                statusText.setText("Invalid username or password!");
            } else if (messageSplit[0].equals("login-success")) {
                statusText.setText("Login successful!");
                // Logic for successful login can be added here
            } else if (messageSplit[0].equals("duplicate-login")) {
                statusText.setText("User is already logged in.");
            }
        });
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
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
