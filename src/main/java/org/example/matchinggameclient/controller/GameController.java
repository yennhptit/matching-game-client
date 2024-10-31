package org.example.matchinggameclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameController {

    @FXML
    private Label gameLabel;
    
    public void setGameLabelText(String s)
    {
    	gameLabel.setText(s);
    }

}
