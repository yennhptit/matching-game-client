package org.example.matchinggameclient.controller;

import javafx.stage.Modality;
import org.example.matchinggameclient.model.*;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SocketHandle implements Runnable {

    private static SocketHandle instance;
    private BufferedWriter outputWriter;
    private Socket socketOfClient;
    private LoginController loginController;
    private SignupController signupController;
    private HomeController homeController;
    private GameController gameController;
    private ChattingController chattingController;
    private MatchHistoryController matchHistoryController;
    private PracticeGameController practiceGameController;
    public User client = new User();
    public ArrayList<User> userList = new ArrayList<>();
    public final ArrayList<Invitation> invitations = new ArrayList<>();
    public String request = "";
    public SocketHandle() {
    }

    @Override
    public void run() {
        try {
            socketOfClient = new Socket("26.150.7.239", 7777);
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
    public void setGameController(GameController gameController) {
        this.gameController = gameController; // Set the HomeController
    }
    public void setMatchHistoryController(MatchHistoryController matchHistoryController) {
        this.matchHistoryController = matchHistoryController; // Set the HomeController
    }

    public void setPracticeGameController(PracticeGameController practiceGameController) {
        this.practiceGameController = practiceGameController;
    }

    public void setChattingController(ChattingController chattingController) {
        this.chattingController = chattingController; // Set the ChattingController
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
                clearFile();
                signupController.setDuplicateUsername();
                break;
            case "back-to-login":
                clearFile();
                homeController.backToLogin();
                break;
            case "chat-server":
                homeController.addMessage(messageSplit[1]);
                saveMessageToFile(messageSplit[1]);
                System.out.println(message);
                if (messageSplit[1].contains("online") || messageSplit[1].contains("offline")) {
                    System.out.println("Cập nhật danh sách người chơi");
                    write("get-rank-charts");
                    request = "update-list";
                }
                String temp = messageSplit[1];
                String[] tempSplit = temp.split(":", 2);
                String username = tempSplit[0];
                if (tempSplit[1].contains("online") && chattingController != null) {
                    chattingController.updateOnlineStatus(username, true);
                } else if (tempSplit[1].contains("offline") && chattingController != null) {
                    chattingController.updateOnlineStatus(username, false);
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
            case "invitation-unavailable":
                JOptionPane.showMessageDialog(null, "Invitation is unavailable");
                break;
            case "get-card":
            	Platform.runLater(() -> { // Chạy trong luồng FX
                    try {
                    	homeController.cancelButtonClicked();

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/game_view.fxml"));
                        Parent root = loader.load();
                        GameController controller = loader.getController();

                        Long matchId = Long.parseLong(messageSplit[1]);
                        int otherUserID = Integer.parseInt(messageSplit[2]);
                        Integer opponentID = null;
                        String opponentUsername = null;
                        for(User u : userList)
                        {
                        	if(u.getID() == otherUserID)
                        	{
//                        		controller.setGameLabelText(client.getUsername()
//                        				+ " vs " + u.getUsername());
                                opponentID = u.getID();
                                opponentUsername = u.getUsername();
                        	}
                        }
                        homeController.homeToGame(matchId, opponentID, opponentUsername, getListCard(message), controller);
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
            case "end-match-exit-success":
                if(client.getID() == Integer.parseInt(messageSplit[1])){
                    this.request = "game";
                    write("get-rank-charts");
                }
                break;
            case "get-result":
                if(Long.parseLong(messageSplit[1]) == gameController.getMatchId()){
                    gameController.checkFinalScore(messageSplit[4]);
                }
                break;
            case "update-opponent-point":
                System.out.println(gameController.getMatchId());
                System.out.println(client.getID());
                if(Long.parseLong(messageSplit[1]) == gameController.getMatchId() && Integer.parseInt(messageSplit[2]) == client.getID()){
                    gameController.updateOpponentPoint();
                }
                break;
            case "get-history":
                if(Long.parseLong(messageSplit[1]) == client.getID()) {
                    Platform.runLater(() -> {
                        homeController.cancelButtonClicked();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/match_history_view.fxml"));
                        Parent root = null;
                        try {
                            root = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        MatchHistoryController controller = loader.getController();

                        homeController.homeToHistory(getHistory(messageSplit), controller);
                        Stage stage = homeController.getStage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Memory Game");
                        stage.show();
                    });
                }
                break;
            case "home-to-history-success":
                this.request = "history";
                write("get-rank-charts");
                break;
            case "practice-to-home-success":
                this.request = "practice";
                write("get-rank-charts");
                break;
            case "get-history-popup":
                Platform.runLater(() -> {
                    homeController.cancelButtonClicked();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/player_info_popup.fxml"));
                    Parent root = null;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    PlayerInfoController controller = loader.getController();

                    controller.setPlayerInfo(messageSplit[3],messageSplit[4],messageSplit[5],messageSplit[6],messageSplit[7]);

                    Stage popupStage = new Stage();
                    popupStage.initModality(Modality.APPLICATION_MODAL);
                    popupStage.setTitle("Player Information");

                    Scene scene = new Scene(root, 300, 200); // Set kích thước popup
                    popupStage.setScene(scene);
                    popupStage.showAndWait();
                });
                break;
            case "receive-message-from-user":
                String[] messageSplit2 = message.split(",", 4);
                String type = messageSplit2[1];
                int senderId = Integer.parseInt(messageSplit2[2]);
                String messageContent = messageSplit2[3];
                String stringTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                chattingController.updateReceiveChat(type, senderId, messageContent, stringTime);

            case "return-get-list-message":
                chattingController.updateChatting(messageSplit);
                break;
            case "return-get-last-message":
                handleLastMess(messageSplit);
                chattingController.loadChatItems(chattingController.latestMessages);
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
        clearFile();
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
        } else if (request.equals("game")){
            gameController.getTimer().stop();
            gameController.gameToHome(client.getID(), invitations, userList, "");
        } else if (request.equals("history")){
            matchHistoryController.historyToHome(client.getID(), invitations, userList, "");
        }else if (request.equals("practice")){
            practiceGameController.practiceToHome(client.getID(), invitations, userList, "");
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

    private List<Card> getListCard(String message) {
        List<Card> cards = new ArrayList<>();
        String[] splitStrings = message.split(",");

        for (int i = 4; i < splitStrings.length - 1; i += 2) {
            // Loại bỏ ký tự không mong muốn, khoảng trắng và xử lý chuỗi hình ảnh
            String countStr = splitStrings[i].replaceAll("[()]", "").trim();
            String imageStr = splitStrings[i + 1].replaceAll("[()]", "").trim();

            try {
                int count = Integer.parseInt(countStr); // Chuyển đổi chuỗi thành số nguyên
                cards.add(new Card(count, imageStr));
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in: " + countStr);
                // Bỏ qua mục này nếu không thể parse
            }
        }
        return cards;
    }

    private List<MatchHistory> getHistory(String[] messageSplit) {
        List<MatchHistory> matchHistories = new ArrayList<>();

        if (messageSplit.length < 3) {
            return matchHistories; // Return an empty list if there is no history data
        }

        String matchHistoryStr = messageSplit[2];

        for (int i = 2; i < messageSplit.length; i+=5) {
            try {
                Integer userId = Integer.parseInt(messageSplit[i+1].replaceAll("[()]", "").trim());
                Long matchId = Long.parseLong(messageSplit[i].replaceAll("[()]", "").trim());
                String result = messageSplit[i+2].replaceAll("[()]", "").trim();
                Integer pointsEarned = Integer.parseInt(messageSplit[i+3].replaceAll("[()]", "").trim());
                LocalDateTime createdAt = LocalDateTime.parse(messageSplit[i+4].replaceAll("[()]", "").trim());

                matchHistories.add(new MatchHistory(userId, matchId, result, pointsEarned, createdAt));
            } catch (Exception e) {
                // Handle parsing exceptions (if any)
                e.printStackTrace();
            }
        }

        return matchHistories;
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
    private void handleLastMess(String[] messageSplit) {
        chattingController.latestMessages = new ArrayList<>();
        for (int i = 1; i < messageSplit.length; i += 6) {
            if (i + 5 < messageSplit.length) {
                Message message = createMessFromMessage(messageSplit, i);
                chattingController.latestMessages.add(message); // Add user to user list
            }
        }

    }

    private Message createMessFromMessage (String[] messageSplit, int index) {
        return new Message(
                Integer.parseInt(messageSplit[index]),
                Integer.parseInt(messageSplit[index + 1]),
                Integer.parseInt(messageSplit[index + 2]),
                messageSplit[index + 3],
                LocalDateTime.parse(messageSplit[index + 4]),
                messageSplit[index + 5]
        );


    }
    public void saveMessageToFile(String message) {
        String fileName = "src/main/java/org/example/matchinggameclient/data/" + client.getID() + ".txt";
        System.out.println("Saving message to file: " + fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(message);
            writer.newLine(); // Thêm dòng mới sau mỗi thông điệp
        } catch (IOException e) {
            e.printStackTrace(); // Xử lý ngoại lệ nếu có lỗi
        }
    }

    public void clearFile() {
        String fileName = "src/main/java/org/example/matchinggameclient/data/" + client.getID() + ".txt";

        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(""); // Ghi một chuỗi rỗng để xóa nội dung file
        } catch (IOException e) {
            e.printStackTrace(); // Xử lý ngoại lệ nếu có lỗi
        }
    }

}
