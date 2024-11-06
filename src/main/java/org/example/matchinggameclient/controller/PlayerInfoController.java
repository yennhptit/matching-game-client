package org.example.matchinggameclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PlayerInfoController {

    @FXML
    private Label nameLabel;
    @FXML
    private Label starsLabel;
    @FXML
    private Label rankLabel;
    @FXML
    private Label matchesPlayedLabel;
    @FXML
    private Label matchesWonLabel;
    @FXML
    private Label matchesLostLabel;
    @FXML
    private Label matchesDrawnLabel;

    public void setPlayerInfo(String name, String stars, String matchesWon, String matchesLost, String matchesDrawn) {
        nameLabel.setText(name);
        starsLabel.setText("Stars: " + stars);
//        rankLabel.setText("Rank: " + rank);
        matchesPlayedLabel.setText("Matches Played: " + (Long.parseLong(matchesWon) + Long.parseLong(matchesLost)+ Long.parseLong(matchesDrawn)) );
        matchesWonLabel.setText("Wins: " + matchesWon);
        matchesLostLabel.setText("Losses: " + matchesLost);
        matchesDrawnLabel.setText("Draws: " + matchesDrawn);
    }



    @FXML
    private void handleClose() {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}
