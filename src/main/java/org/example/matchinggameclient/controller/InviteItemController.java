package org.example.matchinggameclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class InviteItemController {

    @FXML
    private AnchorPane inviteItem; // Đảm bảo rằng fx:id="inviteItem" trong FXML khớp với tên biến này

    @FXML
    private ImageView backImage; // Biến cho ImageView

    @FXML
    private Button button1; // Biến cho nút 1
    @FXML
    private Button button2; // Biến cho nút 2
    @FXML
    private Button button3; // Biến cho nút 3

    // Phương thức khởi tạo hoặc các phương thức khác có thể được thêm vào đây
    @FXML
    public void initialize() {
        // Đây là nơi bạn có thể khởi tạo hoặc cấu hình các thành phần giao diện người dùng
        setupButtons();
    }

    private void setupButtons() {
        // Thêm sự kiện cho nút 1
        button1.setOnAction(event -> {
            // Logic khi nút 1 được nhấn
            System.out.println("Nút 1 được nhấn!");
        });

        // Thêm sự kiện cho nút 2
        button2.setOnAction(event -> {
            // Logic khi nút 2 được nhấn
            System.out.println("Nút 2 được nhấn!");
        });

        // Thêm sự kiện cho nút 3
        button3.setOnAction(event -> {
            // Logic khi nút 3 được nhấn
            System.out.println("Nút 3 được nhấn!");
        });
    }
}
