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

public class SignupController implements MessageHandler {

    @FXML
    private TextField usernametextField; // Reference to the username input field

    @FXML
    private TextField passwordtextField; // Reference to the password input field

    @FXML
    private Text statusText; // Reference to status text

    @FXML
    private Text loginLabel; // Reference to the 'Đăng nhập' Text node

    private SocketHandle socketHandle; // Instance of SocketHandle

    public SignupController() {
        // SocketHandle will be initialized in the controller
    }

    @FXML
    public void initialize() {
        socketHandle = new SocketHandle(this); // Pass the current controller as the message handler
        new Thread(socketHandle).start(); // Start the socket communication in a separate thread
        statusText.setText("");

        // Clear statusText when the user types in the fields
        usernametextField.setOnKeyReleased(event -> clearStatusText());
        passwordtextField.setOnKeyReleased(event -> clearStatusText());

    }

    @FXML
    private void handleSignupBtn(ActionEvent event) {
        String username = usernametextField.getText().trim(); // Get trimmed username
        String password = passwordtextField.getText().trim(); // Get trimmed password

        // Validate that all fields are filled
        if (username.isEmpty() || password.isEmpty()) {
            statusText.setText("All fields are required!"); // Set status text for empty fields
            return; // Exit the method if validation fails
        }

        // If validation passes, create request and send it
        String request = "register," + username + "," + password;
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
            switch (messageSplit[0]) {
                case "login-success":
                    statusText.setText("Signup successful! You can now log in.");
                    // Logic for successful signup can be added here (optional)
                    break;
                case "duplicate-username":
                    statusText.setText("Username is already taken!");
                    break;
//                default:
//                    statusText.setText("Unknown response from server.");
//                    break;
            }
        });
    }

    private void clearStatusText() {
        statusText.setText(""); // Clear the status text
    }

    @FXML
    private void handleLoginLabel(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
