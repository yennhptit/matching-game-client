package org.example.matchinggameclient.controller;

import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.example.matchinggameclient.model.Invitation;
import org.example.matchinggameclient.model.User;

public class HomeController{
    public final int ALL_FILTER = 0;
    public final int ONLINE_FILTER = 1;

    @FXML
    private Button cancelButton;

//    @FXML
//    private TextArea chatTextArea;

    @FXML
    private TextField chatTextField;

    @FXML
    private Button clientHistoryButton;

    @FXML
    private Label clientInfoLabel;

    @FXML
    private Button clientLogoutButton;

    @FXML
    private Button findMatchButton;

    @FXML
    private VBox invitationsContent;

    @FXML
    private Label playModeLabel;

    @FXML
    private VBox playerListContent;

    @FXML
    private Button practiceButton;

    @FXML
    private Button searchButton;

    @FXML
    private ComboBox<String> searchFilterComboBox;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button sendButton;

    @FXML
    private VBox messageContainer; // VBox để chứa các tin nhắn

    private SocketHandle socketHandle; // Instance of SocketHandle

    private User client;
    private ArrayList<Invitation> invitationList;
    public ArrayList<User> playerList;
    private String chatServerContent;

    @FXML
    private void initialize()
    {
        socketHandle = SocketHandle.getInstance(); // Pass the current controller as the message handler
        socketHandle.setHomeController(this);
//        new Thread(socketHandle).start(); // Start the socket communication in a separate thread

        searchFilterComboBox.getItems().addAll("All", "Online");
        searchFilterComboBox.setValue(searchFilterComboBox.getItems().get(0));
        cancelButton.setVisible(false);

        searchFilterComboBox.setOnAction(event -> {
            updatePlayerList();
        });

        searchTextField.setOnAction(event -> {
            updatePlayerList();
        });

        searchButton.setOnAction(event -> {
            updatePlayerList();
        });

        clientHistoryButton.setOnAction(event -> {
            clientHistoryButtonClicked();
        });

        clientLogoutButton.setOnAction(event -> {
            clientLogoutButtonClicked();
        });

        sendButton.setOnAction(event -> {
            try {
                sendButtonClicked();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        chatTextField.setOnAction(event -> {
            try {
                sendButtonClicked();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        findMatchButton.setOnAction(event -> {
            findMatchButtonClicked();
        });

        practiceButton.setOnAction(event -> {
            practiceButtonClicked();
        });

        cancelButton.setOnAction(event -> {
            cancelButtonClicked();
        });
    }

    public void loadData(int clientId, ArrayList<Invitation> invitationList,
                         ArrayList<User> playerList, String chatServerContent)
    {
        this.invitationList = invitationList;
        this.playerList = playerList;
        this.chatServerContent = chatServerContent;
        setClient(clientId);
        updatePlayerList();
        updateInvitationList();
//        updateChatServerContent();
    }
    
    public Stage getStage()
    {
    	return (Stage) clientInfoLabel.getScene().getWindow();
    }


    private void setClient(int id)
    {
        for(User u : playerList)
        {
            if(id == u.getID())
            {
                this.client = u;
            }
        }
        clientInfoLabel.setText(String.format("Player: %s    Rank: #%03d    Stars: %d",
                client.getUsername(), client.getRank(), client.getStar()));
    }

    @FXML
    public void updatePlayerList()
    {
        String searchText = searchTextField.getText();
        int filter = searchFilterComboBox.getSelectionModel().getSelectedIndex();
        Platform.runLater(() -> {
            playerListContent.getChildren().clear();
            System.out.println("Player list updated: " + playerListContent.getChildren().size());
            System.out.println(playerList.size());
            for (int i = 0; i < playerList.size(); i++) {
                User u = playerList.get(i);
                if (u.getUsername().contains(searchText) && checkFilter(filter, u.isOnline())) {
                    insertUserItem(u);
                }
            }
            System.out.println("Player list updated: " + playerListContent.getChildren().size());
        });
    }


    private boolean checkFilter(int filter, boolean isOnline)
    {
        if(filter == ALL_FILTER || filter == ONLINE_FILTER && isOnline)
        {
            return true;
        }
        return false;
    }
    
    @FXML
    public void updateInvitationList()
    {
        invitationsContent.getChildren().clear();
        for(Invitation inv : invitationList)
        {
            insertInvitationItem(inv);
        }
    }

//    public void updateChatServerContent()
//    {
//        chatTextArea.setText(chatServerContent);
//    }

    private void insertUserItem(User user)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/UserItem.fxml"));
            GridPane item = loader.load();
            UserItemController controller = loader.getController();
            controller.init(user, socketHandle);
            if(user.getID() == client.getID())
            {
                controller.hideInviteButton();
            }
            playerListContent.getChildren().add(item);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertInvitationItem(Invitation invitation)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/InvitationItem.fxml"));
            GridPane item = loader.load();
            InvitationItemController controller = loader.getController();
            controller.init(invitation, socketHandle);

            invitationsContent.getChildren().add(item);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clientLogoutButtonClicked() {
        try {
            // Gửi thông điệp để thông báo rằng người dùng đã đăng xuất
            socketHandle.write("offline," + client.getID());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void clientHistoryButtonClicked()
    {
        System.out.println("Show client history");
    }

    private void sendButtonClicked() throws IOException {
        String chatMessage = chatTextField.getText();
        chatTextField.setText("");
//        System.out.println("Send chat: " + chatMessage);
        addMessage("You: " + chatMessage);
       socketHandle.write("chat-server," + chatMessage);
    }

    private void findMatchButtonClicked()
    {
        playModeLabel.setText("Finding match...");
        cancelButton.setVisible(true);
        findMatchButton.setDisable(true);
        practiceButton.setDisable(true);
    }

    private void practiceButtonClicked()
    {
        playModeLabel.setText("Start practicing...");
        cancelButton.setVisible(true);
        findMatchButton.setDisable(true);
        practiceButton.setDisable(true);
    }

    private void cancelButtonClicked()
    {
        playModeLabel.setText("Choose a play mode");
        cancelButton.setVisible(false);
        findMatchButton.setDisable(false);
        practiceButton.setDisable(false);
    }
    public void backToLogin() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) clientLogoutButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public void addMessage(String message) {
        // Kiểm tra xem thông điệp có dạng [16] hay không
        if (message.matches("\\[\\d+\\].*")) {
            // Tách số trong ngoặc vuông và phần còn lại
            String[] parts = message.split(" ", 2);

            // Tạo phần văn bản cho số trong ngoặc vuông
            Text part1 = new Text(parts[0] + " "); // Đây là [16]
            part1.setFill(Color.RED); // Đặt màu đỏ cho phần này
            part1.setFont(Font.font("System", FontWeight.BOLD, 15)); // Đặt cỡ chữ 15 và in đậm

            // Tạo phần văn bản cho phần còn lại
            Text part2 = new Text(parts.length > 1 ? parts[1] : ""); // Đây là abc đang online
            part2.setFill(Color.BLACK); // Đặt màu đen cho phần này
            part2.setFont(Font.font(15)); // Đặt cỡ chữ 15

            // Tạo một TextFlow để chứa các phần văn bản
            TextFlow textFlow = new TextFlow(part1, part2);

            // Thêm TextFlow vào VBox
            Platform.runLater(() -> {
                messageContainer.getChildren().add(textFlow);
            });
        } else {
            // Tách thông điệp thành hai phần nếu có dấu ':'
            String[] messageSplit = message.split(":", 2);
            if (messageSplit.length > 1) {
                // Tạo phần văn bản cho phần đầu tiên
                Text part1 = new Text(messageSplit[0] + ":"); // Ví dụ: "Server:"
                part1.setFill(Color.RED); // Đặt màu đỏ cho phần đầu
                part1.setFont(Font.font("System", FontWeight.BOLD, 15)); // Đặt cỡ chữ 15 và in đậm

                // Tạo phần văn bản cho phần còn lại
                Text part2 = new Text(messageSplit[1]);
                part2.setFill(Color.BLACK); // Đặt màu đen cho phần còn lại
                part2.setFont(Font.font(15)); // Đặt cỡ chữ 15

                // Tạo một TextFlow để chứa các phần văn bản
                TextFlow textFlow = new TextFlow(part1, part2);

                // Thêm TextFlow vào VBox
                Platform.runLater(() -> {
                    messageContainer.getChildren().add(textFlow);
                });
            } else {
                // Nếu không có phần nào được tách ra, thêm toàn bộ thông điệp
                Platform.runLater(() -> {
                    Text fullMessage = new Text(message);
                    fullMessage.setFill(Color.BLACK);
                    fullMessage.setFont(Font.font(15));
                    messageContainer.getChildren().add(fullMessage);
                });
            }
        }
    }


}
