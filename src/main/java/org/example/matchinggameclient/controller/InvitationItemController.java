package org.example.matchinggameclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.example.matchinggameclient.model.Invitation;

public class InvitationItemController {
    @FXML
    private Button acceptButton;

    @FXML
    private Button declineButton;

    @FXML
    private Label senderNameLabel;

    @FXML
    private Label senderRankLabel;

    @FXML
    private Label senderStarsLabel;

    private Invitation invitation;

    public void init(Invitation invitation)
    {
        this.invitation = invitation;

        acceptButton.setOnAction(event -> {
            acceptButtonClicked();
        });

        declineButton.setOnAction(event -> {
            declineButtonClicked();
        });

        senderNameLabel.setText(invitation.getSenderName());
        senderRankLabel.setText(String.format("#%03d", invitation.getSenderRank()));
        senderStarsLabel.setText("" + invitation.getSenderStars());
    }

    private void acceptButtonClicked()
    {
        System.out.println("Accept Invitation of " + invitation.getSenderName());
    }

    private void declineButtonClicked()
    {
        System.out.println("Decline Invitation of " + invitation.getSenderName());
        if (declineButton.getParent().getParent() instanceof Pane) {
            Pane parent = (Pane) declineButton.getParent().getParent();
            parent.getChildren().remove(declineButton.getParent());
        }
    }
}
