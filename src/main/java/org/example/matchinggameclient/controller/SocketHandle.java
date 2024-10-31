package org.example.matchinggameclient.controller;

import org.example.matchinggameclient.model.Invitation;
import org.example.matchinggameclient.model.User;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class SocketHandle implements Runnable {

    private static SocketHandle instance;
    private BufferedWriter outputWriter;
    private Socket socketOfClient;
    private LoginController loginController;
    private SignupController signupController;
    private HomeController homeController;
    public User client = new User();
    public ArrayList<User> userList = new ArrayList<>();
    public final ArrayList<Invitation> invitations = new ArrayList<>();
    public String request = "";
    public SocketHandle() {
    }

    @Override
    public void run() {
        try {
            socketOfClient = new Socket("127.0.0.1", 7777);
            outputWriter = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
            String message;
            while ((message = inputReader.readLine()) != null) {
                onMessageReceived(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SocketHandle getInstance() {
        if (instance == null) {
            instance = new SocketHandle();
            new Thread(instance).start();
        }
        return instance;
    }

    public void setLoginController(LoginController controller) {
        this.loginController = controller; // Set the LoginController
    }
    public void setSignupController(SignupController signupController)
    {
        this.signupController = signupController;
    }
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController; // Set the HomeController
    }


    public void write(String message) throws IOException {
        if (outputWriter != null) {
            outputWriter.write(message);
            outputWriter.newLine();
            outputWriter.flush();

        } else {
            throw new IOException("OutputWriter is not initialized.");
        }
    }

    public Socket getSocketOfClient() {
        return socketOfClient;
    }

    // Method to handle received messages
    private void onMessageReceived(String message) throws IOException {
        String[] messageSplit = message.split(","); // Split message by commas
        System.out.println("Received message: " + message);

        switch (messageSplit[0]) {
            case "wrong-user":
                loginController.setTextWrongUser();
                break;
            case "login-success":
                handleLoginSuccess(messageSplit);
                break;
            case "dupplicate-login":
                loginController.setDuplicateLogin();
                break;
            case "return-get-rank-charts":
                handleRankCharts(messageSplit);
                break;
            case "duplicate-username":
                signupController.setDuplicateUsername();
                break;
            case "back-to-login":
                homeController.backToLogin();
                break;
            case "chat-server":
                homeController.addMessage(messageSplit[1]);
                System.out.println(messageSplit[1]);
                if (messageSplit[1].contains("online") || messageSplit[1].contains("offline")) {
                    System.out.println("Cập nhâật danh sách người chơi");
                    write("get-rank-charts");
                    request = "update-list";
                }
                break;
            case "add-invitation":
            	int senderID = Integer.parseInt(messageSplit[1]);
            	for(User sender : userList)
            	{
            		if(sender.getID() == senderID)
            		{
            			invitations.add(new Invitation(sender));
            			Platform.runLater(() -> {
            				homeController.updateInvitationList();
            			});
            			break;
            		}
            	}
            	break;
            case "remove-invitation":
            	for(Invitation inv : invitations)
            	{
            		if(inv.getSenderID() == Integer.parseInt(messageSplit[1]))
            		{
            			invitations.remove(inv);
            			Platform.runLater(() -> {
            				homeController.updateInvitationList();
            			});
            			break;
            		}
            	}
            	break;
            case "start-match":
            	Platform.runLater(() -> { // Chạy trong luồng FX
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/Game.fxml"));
                        Parent root = loader.load();
                        GameController controller = loader.getController();
                        
                        int otherUserID = Integer.parseInt(messageSplit[1]);
                        for(User u : userList)
                        {
                        	if(u.getID() == otherUserID)
                        	{
                        		controller.setGameLabelText(client.getUsername() 
                        				+ " vs " + u.getUsername());
                        	}
                        }
                        
                        // Cập nhật giai điệu và ẩn cảnh hiện tại
                        Stage stage = homeController.getStage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Memory Game");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace(); // Xử lý IOException
                    }
                });
            	break;
            case "invitation-unavailable":
            	JOptionPane.showMessageDialog(null, "Invitation is unavailable");
            	break;
            default:
                System.out.println("Unknown message: " + message);
                break;
        }
    }

    // Handle successful login
    private void handleLoginSuccess(String[] messageSplit) throws IOException {
        client = new User(
                Integer.parseInt(messageSplit[1]),
                messageSplit[2],
                messageSplit[3],
                Integer.parseInt(messageSplit[4]),
                Integer.parseInt(messageSplit[5]),
                Integer.parseInt(messageSplit[6]),
                Boolean.parseBoolean(messageSplit[7]),
                Boolean.parseBoolean(messageSplit[8]),
                Integer.parseInt(messageSplit[9]),
                Integer.parseInt(messageSplit[10])
        );
        if (client != null) {
            System.out.println(client.toString());
        }
        write("get-rank-charts"); // Request rank charts after login success
    }

    // Handle received rank charts
    private void handleRankCharts(String[] messageSplit) {
         userList = new ArrayList<>();
        for (int i = 1; i < messageSplit.length; i += 10) {
            if (i + 9 < messageSplit.length) {
                User user = createUserFromMessage(messageSplit, i);
                userList.add(user); // Add user to user list
            } else {
                System.err.println("Insufficient data for a User object at index: " + i);
            }
        }
        //createDummyInvitations(); // Create dummy invitations for testing
        if (request.equals("register"))
            signupController.loginToHome(client.getID(), invitations, userList, "");
        else if (request.equals("login"))
            loginController.loginToHome(client.getID(), invitations, userList, ""); // Navigate to home
        else if (request.equals("update-list")) {
            homeController.playerList = userList;
            homeController.updatePlayerList();
        }

    }

    // Create User object from message data
    private User createUserFromMessage(String[] messageSplit, int index) {
        return new User(
                Integer.parseInt(messageSplit[index]),
                messageSplit[index + 1],
                messageSplit[index + 2],
                Integer.parseInt(messageSplit[index + 3]),
                Integer.parseInt(messageSplit[index + 4]),
                Integer.parseInt(messageSplit[index + 5]),
                Boolean.parseBoolean(messageSplit[index + 6]),
                Boolean.parseBoolean(messageSplit[index + 7]),
                Integer.parseInt(messageSplit[index + 8]),
                Integer.parseInt(messageSplit[index + 9])
        );
    }

    // Create dummy invitations for testing purposes
    private void createDummyInvitations() {
        for (int i = 0; i < 12; i++) {
            boolean isOnline = (i % 2 == 0);
            User u = new User(i, "player" + i, i * 10, isOnline);
            u.setRank(i);
            Invitation inv = new Invitation(u);
            invitations.add(inv); // Add invitation to the list
        }
    }
}
