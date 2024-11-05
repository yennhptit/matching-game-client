package org.example.matchinggameclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class ChatItemController {
    @FXML
    private Circle onlineStatus;

    @FXML
    private Label username; // Đặt tên này tương ứng với label trong FXML

    @FXML
    private HBox chatItemHBox; // Tham chiếu đến HBox trong FXML

    @FXML
    private Label latestMess; // Thêm label để hiển thị tin nhắn mới nhất

    private int userID; // Thêm biến userID để lưu ID của người dùng

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUsername(String usernameStr) {
        if (username != null) {
            username.setText(usernameStr);
        }
    }
    public void setLatestMess(String latestMessStr) {
        if (latestMess != null) {
            latestMess.setText(latestMessStr);
        }
    }



    // Hàm để cập nhật trạng thái online (bạn đã có rồi)
    public void setOnlineStatus(boolean isOnline) {
        if (onlineStatus != null) {
            if (isOnline) {
                onlineStatus.setFill(javafx.scene.paint.Color.GREEN); // Màu xanh khi online
            } else {
                onlineStatus.setFill(javafx.scene.paint.Color.GRAY); // Màu đỏ khi offline
            }
        }
    }
    public void changeBackgroundColor(String color) {
        chatItemHBox.setStyle("-fx-background-color: " + color + ";");
    }

    // Phương thức để thiết lập trạng thái được chọn
    public void setSelected(boolean selected) {
        if (selected) {
            changeBackgroundColor("#FFA24C"); // Màu vàng khi được chọn
        } else {
            changeBackgroundColor("#8ABFA3"); // Màu xám khi không được chọn
        }
    }

    // Thêm vào ChatItemController.java
    public String getUsername() {
        return username.getText(); // Hoặc trả về biến username nếu bạn có
    }

    public boolean isOnline() {
        return onlineStatus.getFill() == javafx.scene.paint.Color.GREEN; // Kiểm tra màu sắc để xác định trạng thái online
    }
    public HBox getChatItemHBox() {
        return chatItemHBox;
    }


}