package org.example.matchinggameclient.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.IOException;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.matchinggameclient.model.User;

public class UserItemController {
    @FXML
    private Button historyButton;

    @FXML
    private Button inviteButton;

    @FXML
    private Label playerNameLabel;

    @FXML
    private Label rankLabel;

    @FXML
    private Label starsLabel;

    private User user;
    private SocketHandle socketHandle;

    public void init(User user, SocketHandle socketHandle)
    {
        this.user = user;
        this.socketHandle = socketHandle;

        historyButton.setOnAction(event -> {
            historyButtonClicked();
        });

        inviteButton.setOnAction(event -> {
            inviteButtonClicked();
        });

        playerNameLabel.setText(user.getUsername());
        if(!user.isOnline())
        {
            playerNameLabel.setTextFill(Color.rgb(220, 220, 220));
            hideInviteButton();
        }
        rankLabel.setText(String.format("#%03d", user.getRank()));
        starsLabel.setText("" + user.getStar());
    }

    private void historyButtonClicked()
    {
        System.out.println("Show history of " + user.getUsername());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/player_info_popup.fxml"));
            Parent root = loader.load();

            // Lấy dữ liệu người chơi từ server và truyền vào Controller của popup
            PlayerInfoController controller = loader.getController();
            controller.setPlayerInfo("PlayerName", 120, 5, 50, 30, 15, 5);

            // Tạo và hiển thị popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Player Information");

            Scene scene = new Scene(root, 300, 200); // Set kích thước popup
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inviteButtonClicked()
    {
        System.out.println("Invite " + user.getUsername());
        try {
			socketHandle.write("invite," + user.getID());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void hideInviteButton()
    {
        inviteButton.setVisible(false);
    }
}
