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
import org.example.matchinggameclient.home.HomeRun;
import org.example.matchinggameclient.model.User;
//import org.example.matchinggameclient.home.HomeRun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private User client;

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
            System.out.println(messageSplit[0]);
            if (messageSplit[0].equals("wrong-user")) {
                statusText.setText("Invalid username or password!");
            } else if (messageSplit[0].equals("login-success")) {
                statusText.setText("Login successful!");

                try {
                    // Open the Swing-based Home page

                    // int ID, String username, String password, int numberOfGame, int numberOfWin, int numberOfDraw, boolean isOnline, boolean isPlaying, int star, int rank
                    client = new User(Integer.parseInt(messageSplit[1]), messageSplit[2], messageSplit[3], Integer.parseInt(messageSplit[4]), Integer.parseInt(messageSplit[5]), Integer.parseInt(messageSplit[6]), Boolean.parseBoolean(messageSplit[7]), Boolean.parseBoolean(messageSplit[8]), Integer.parseInt(messageSplit[9]), Integer.parseInt(messageSplit[10]));

                    if (client != null) {
                        System.out.println(client.toString());
                    }
                    socketHandle.write("get-rank-charts");
//
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                 Logic for successful login can be added here
            } else if (messageSplit[0].equals("dupplicate-login")) {
                statusText.setText("User is already logged in.");
            } else if (messageSplit[0].equals("return-get-rank-charts")) {
                System.out.println(client.toString());
                ArrayList<User> userList = new ArrayList<>();
                for (int i = 1; i < messageSplit.length; i += 10) { // Move in steps of 10 for each user
                    if (i + 9 < messageSplit.length) { // Ensure there are enough parts to create a User
                        int id = Integer.parseInt(messageSplit[i]);
                        String username = messageSplit[i + 1];
                        String password = messageSplit[i + 2]; // 'null' can be handled if necessary
                        int numberOfGame = Integer.parseInt(messageSplit[i + 3]);
                        int numberOfWin = Integer.parseInt(messageSplit[i + 4]);
                        int numberOfDraw = Integer.parseInt(messageSplit[i + 5]);
                        boolean isOnline = Boolean.parseBoolean(messageSplit[i + 6]);
                        boolean isPlaying = Boolean.parseBoolean(messageSplit[i + 7]);
                        int star = Integer.parseInt(messageSplit[i + 8]);
                        int rank = Integer.parseInt(messageSplit[i + 9]);

                        // Create a User object and add it to the list
                        User user = new User(id, username, password, numberOfGame, numberOfWin, numberOfDraw, isOnline, isPlaying, star, rank);
                        System.out.println(user.toString());
                        userList.add(user);
                    } else {
                        System.err.println("Insufficient data for a User object at index: " + i);
                    }
                }
                HomeRun.runHome(client, userList);
                // Optionally, hide the login window
                Stage stage = (Stage) statusText.getScene().getWindow();
                stage.close();
            }
            else {
                System.out.println("Unknown message: " + message);
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
