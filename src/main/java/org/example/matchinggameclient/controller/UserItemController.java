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
    private Integer clientId;
    private SocketHandle socketHandle;

    public void init(User user, Integer clientId, SocketHandle socketHandle)
    {
        this.user = user;
        this.socketHandle = socketHandle;
        this.clientId = clientId;

        historyButton.setOnAction(event -> {
            try {
                historyButtonClicked();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    private void historyButtonClicked() throws IOException {
        System.out.println("Show history of " + user.getUsername());
        socketHandle.write("show-history-popup,"+ clientId + "," +user.getID() );

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
