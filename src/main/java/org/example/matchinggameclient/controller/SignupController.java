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
import org.example.matchinggameclient.model.Invitation;
import org.example.matchinggameclient.model.User;

import java.io.IOException;
import java.util.ArrayList;

public class SignupController {

    @FXML
    private TextField usernametextField; // Reference to the username input field

    @FXML
    private TextField passwordtextField; // Reference to the password input field

    @FXML
    private Text statusText; // Reference to status text

    @FXML
    private Text loginLabel; // Reference to the 'Đăng nhập' Text node

    @FXML
    private Button signupBtn;

    private SocketHandle socketHandle; // Instance of SocketHandle
    private User client;
    private ArrayList <User> userList = new ArrayList<>();

    public SignupController() {
        // SocketHandle will be initialized in the controller
    }

    @FXML
    public void initialize() {
        socketHandle = SocketHandle.getInstance();// Pass the current controller as the message handler
        socketHandle.setSignupController(this);
//        new Thread(socketHandle).start(); // Start the socket communication in a separate thread
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
            socketHandle.request = "register";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onMessageReceived(String message) {
//        Platform.runLater(() -> {
//            String[] messageSplit = message.split(",");
//            switch (messageSplit[0]) {
//                case "login-success":
////                    statusText.setText("Signup successful! You can now log in.");
//                    try {
//                        for (int i = 1; i < messageSplit.length; i += 10) { // Move in steps of 10 for each user
//                            if (i + 9 < messageSplit.length) { // Ensure there are enough parts to create a User
//                                int id = Integer.parseInt(messageSplit[i]);
//                                String username = messageSplit[i + 1];
//                                String password = messageSplit[i + 2]; // 'null' can be handled if necessary
//                                int numberOfGame = Integer.parseInt(messageSplit[i + 3]);
//                                int numberOfWin = Integer.parseInt(messageSplit[i + 4]);
//                                int numberOfDraw = Integer.parseInt(messageSplit[i + 5]);
//                                boolean isOnline = Boolean.parseBoolean(messageSplit[i + 6]);
//                                boolean isPlaying = Boolean.parseBoolean(messageSplit[i + 7]);
//                                int star = Integer.parseInt(messageSplit[i + 8]);
//                                int rank = Integer.parseInt(messageSplit[i + 9]);
//
//                                // Create a User object and add it to the list
//                                client = new User(id, username, password, numberOfGame, numberOfWin, numberOfDraw, isOnline, isPlaying, star, rank);
//                                System.out.println(client.toString());
//                            } else {
//                                System.err.println("Insufficient data for a User object at index: " + i);
//                            }
//                        }
//                        socketHandle.write("get-rank-charts");
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    // Logic for successful signup can be added here (optional)
//                    break;
//                case "duplicate-username":
//                    statusText.setText("Username is already taken!");
//                    break;
//                case "return-get-rank-charts":
//                    for (int i = 1; i < messageSplit.length; i += 10) { // Move in steps of 10 for each user
//                        if (i + 9 < messageSplit.length) { // Ensure there are enough parts to create a User
//                            int id = Integer.parseInt(messageSplit[i]);
//                            String username = messageSplit[i + 1];
//                            String password = messageSplit[i + 2]; // 'null' can be handled if necessary
//                            int numberOfGame = Integer.parseInt(messageSplit[i + 3]);
//                            int numberOfWin = Integer.parseInt(messageSplit[i + 4]);
//                            int numberOfDraw = Integer.parseInt(messageSplit[i + 5]);
//                            boolean isOnline = Boolean.parseBoolean(messageSplit[i + 6]);
//                            boolean isPlaying = Boolean.parseBoolean(messageSplit[i + 7]);
//                            int star = Integer.parseInt(messageSplit[i + 8]);
//                            int rank = Integer.parseInt(messageSplit[i + 9]);
//
//                            // Create a User object and add it to the list
//                            User user = new User(id, username, password, numberOfGame, numberOfWin, numberOfDraw, isOnline, isPlaying, star, rank);
//                            userList.add(user);
//                        } else {
//                            System.err.println("Insufficient data for a User object at index: " + i);
//                        }
//                    }
//                    try {
//                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/Home.fxml"));
//                        Parent root = loader.load();
//                        HomeController homeController = loader.getController();
//                        ArrayList<Invitation> invitationList = new ArrayList<>();
//                        ArrayList<User> playerList = new ArrayList<>();
//                        for (User u : userList) {
//                            System.out.println(u.toString());
//                        }
//                        for(int i = 0; i < 12; i++)
//                        {
//                            boolean isOnline = false;
//                            if(i%2 == 0) isOnline = true;
//                            User u = new User(i, "player" + i, i*10, isOnline);
//                            u.setRank(i);
//                            Invitation inv = new Invitation(u);
//                            playerList.add(u);
//                            invitationList.add(inv);
//                        }
//
//                        homeController.loadData(client.getID(), invitationList, userList, "");
//
//                        Stage stage = (Stage) signupBtn.getScene().getWindow();
//                        stage.setScene(new Scene(root));
//                        stage.show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                default:
//                    System.out.println("Unknown response from server.");
//                    break;
//            }
//        });
//    }

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
    public void setDuplicateUsername(){
        statusText.setText("Username is already taken!");
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
}
