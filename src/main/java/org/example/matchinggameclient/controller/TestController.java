package org.example.matchinggameclient.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import java.io.IOException;

public class TestController {

    @FXML
    private VBox inviteList; // Biến cho VBox chứa các inviteItem

    @FXML
    public void initialize() {
        // Gọi phương thức để thêm 10 inviteItem vào inviteList
        addInviteItems(10);
    }

    private void addInviteItems(int count) {
        for (int i = 0; i < count; i++) {
            try {
                // Tải tệp FXML cho inviteItem
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/InviteItem.fxml")); // Cập nhật đường dẫn
                Node inviteItem = loader.load();

                // Thêm inviteItem vào inviteList
                inviteList.getChildren().add(inviteItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
