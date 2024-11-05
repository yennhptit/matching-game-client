package org.example.matchinggameclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.matchinggameclient.model.Invitation;
import org.example.matchinggameclient.model.Message;
import org.example.matchinggameclient.model.User;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;

public class ChattingController {

    public ArrayList<User> playerList;
    private User client;

    private SocketHandle socketHandle; // Instance of SocketHandle

    public ChatItemController selectedChatItemController; // Để lưu trữ ChatItem được chọn

    @FXML
    private ImageView backToHome;

    @FXML
    private Label user2name;
    @FXML
    private Circle user2status;



    private ArrayList<ChatItemController> chatItemControllers = new ArrayList<>();


    @FXML
    private Label user2statusLabel;
    @FXML
    private TextField seachText;

    @FXML
    private ImageView findBtn;

    @FXML
    private ImageView send; // Nút gửi
    @FXML
    private ImageView clip; // Nút đính kèm
    @FXML
    private TextField message; // TextField nhập tin nhắn


    @FXML
    private VBox listUser; // VBox này nằm trong ScrollPane đã được thiết lập trong FXML

    @FXML
    private VBox vBoxContent; // VBox này nằm trong ScrollPane đã được thiết lập trong FXML

    @FXML
    private ScrollPane scrollPane; // ScrollPane cho hình ảnh
    public ArrayList<Message> latestMessages;

    // Phương thức initialize() sẽ được gọi khi trang được tải
    public void initialize() {

        socketHandle = SocketHandle.getInstance(); // Pass the current controller as the message handler
        socketHandle.setChattingController(this);

        findBtn.setOnMouseClicked(event -> searchChatItems());



    }
    private void searchChatItems() {
        String searchText = seachText.getText().toLowerCase();
        System.out.println("Searching..." + searchText);

        listUser.getChildren().clear();
        chatItemControllers.clear();

        if (!searchText.isEmpty()) {
            for (User u : playerList) {
                if (u.getID() != client.getID() && u.getUsername().toLowerCase().contains(searchText)) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/ChatItem.fxml"));
                        Node chatItem = loader.load();
                        ChatItemController chatItemController = loader.getController();

                        boolean isOnline = u.isOnline();
                        chatItemController.setOnlineStatus(isOnline);
                        chatItemController.setUsername(u.getUsername());
                        chatItemController.setUserID(u.getID());
                        chatItemController.setLatestMess(lastestMessage(u.getID()));

                        chatItem.setOnMouseClicked(event -> {
                            try {
                                handleChatItemClick(chatItemController);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        // Kiểm tra xem chatItem có phải là mục đã chọn trước đó hay không
                        if (selectedChatItemController != null
                                && selectedChatItemController.getUsername().equals(u.getUsername())) {

                            selectedChatItemController = chatItemController;
                            selectedChatItemController.setSelected(true);
                            socketHandle.write("get-list-message," + client.getID() + "," + selectedChatItemController.getUserID());
                        }

                        listUser.getChildren().add(chatItem);
                        chatItemControllers.add(chatItemController);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            loadChatItems(latestMessages);
        }
    }


    public void updateOnlineStatus(String username, boolean isOnline) {
        Platform.runLater(() -> {
            for (ChatItemController controller : chatItemControllers) {
                if (controller.getUsername().equals(username)) {
                    controller.setOnlineStatus(isOnline);
                }
            }
            updatePlayerList(username, isOnline);
        });
    }
    public void updatePlayerList(String username, boolean isOnline) {
       for (User u : playerList) {
              if (u.getUsername().equals(username)) {
                u.setOnline(isOnline);
                break;
              }
       }
    }
    private String lastestMessage(int checkid)
    {
        String temp = "";
        boolean found = false;
        if (latestMessages != null) {
            for (Message message : latestMessages) {
                if (message.getReceiverId() == checkid) {
                    found = true;
                    if (message.getType().equals("image")) {
                        temp = "You: Send an image.";
                    }
                    else {
                        temp = "You: " + message.getContent();
                    }
                    break;
                } else if (message.getSenderId() == checkid) {
                    User userFinder = new User(); // Create an instance of User
                    User u = userFinder.getUserFromID(playerList, checkid);
                    found = true;
                    if (message.getType().equals("image")) {
                        temp =  u.getUsername() + ": Send an image to you.";
                    }
                    else {
                        temp = u.getUsername() + ": "+  message.getContent();
                    }
                    break;
                }
            }
        }
        if (!found) {
            temp = "Send a greeting to the other friend.";
        }
        return temp;
    }



    public void loadChatItems(ArrayList<Message> latestMessages) {
        Platform.runLater(() -> {
        listUser.getChildren().clear(); // Xóa tất cả các phần tử hiện tại trước khi tải lại
        chatItemControllers.clear();


        for (User u : playerList) {
            if (u.getID() != client.getID()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/ChatItem.fxml"));
                    Node chatItem = loader.load();
                    ChatItemController chatItemController = loader.getController();

                    boolean isOnline = u.isOnline();
                    chatItemController.setOnlineStatus(isOnline);
                    chatItemController.setUsername(u.getUsername());
                    chatItemController.setUserID(u.getID());
                    chatItemController.setLatestMess(lastestMessage(u.getID()));

                    chatItem.setOnMouseClicked(event -> {
                        try {
                            handleChatItemClick(chatItemController);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    // Đảm bảo mục đã chọn vẫn được chọn khi reload danh sách
                    if (selectedChatItemController != null
                            && selectedChatItemController.getUsername().equals(u.getUsername())) {

                        selectedChatItemController = chatItemController;
                        selectedChatItemController.setSelected(true);
                        socketHandle.write("get-list-message," + client.getID() + "," + selectedChatItemController.getUserID());
                    }

                    listUser.getChildren().add(chatItem);
                    chatItemControllers.add(chatItemController);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


//         Nếu không có mục nào được chọn ban đầu, chọn mục đầu tiên làm mặc định
        if (selectedChatItemController == null && !playerList.isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/ChatItem.fxml"));
                Node chatItem = loader.load();

                ChatItemController chatItemController = loader.getController();

                boolean isOnline = playerList.get(0).isOnline();
                chatItemController.setOnlineStatus(isOnline);
                chatItemController.setUsername(playerList.get(0).getUsername());
                chatItemController.setUserID(playerList.get(0).getID());
                chatItemController.setLatestMess(lastestMessage(playerList.get(0).getID()));
                chatItem.setOnMouseClicked(event -> {
                    try {
                        handleChatItemClick(chatItemController);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                selectedChatItemController = chatItemController;
                selectedChatItemController.setSelected(true);
                socketHandle.write("get-list-message," + client.getID() + "," + selectedChatItemController.getUserID());
                listUser.getChildren().remove(0); // Xóa node đầu tiên
                //remove phan tu dau tien
                chatItemControllers.remove(0);
                listUser.getChildren().add(0, chatItem); // Thêm vào đầu danh sách
                // add vao dau tien
                chatItemControllers.add(0, chatItemController);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        });
    }

    private void handleChatItemClick(ChatItemController chatItemController) throws IOException {
        // Nếu có một ChatItem được chọn trước đó, đặt lại màu cho nó
        if (selectedChatItemController != null) {
            selectedChatItemController.setSelected(false);

        }
        if (selectedChatItemController != null && selectedChatItemController.getUserID() != chatItemController.getUserID()) {
            vBoxContent.getChildren().clear();
        }

        // Đặt ChatItem hiện tại là được chọn
        chatItemController.setSelected(true);
        selectedChatItemController = chatItemController; // Cập nhật ChatItem được chọn
        socketHandle.write("get-list-message," + client.getID() + "," + selectedChatItemController.getUserID());


        String selectedUsername = chatItemController.getUsername(); // Lấy tên người dùng từ ChatItem
        boolean selectedIsOnline = chatItemController.isOnline(); // Lấy trạng thái online từ ChatItem
        setUser2Info(selectedUsername, selectedIsOnline); // Cập nhật thông tin người dùng
    }

    public void loadData(User client, ArrayList<User> playerList) throws IOException {
        this.client = client;
        this.playerList = playerList;
//        loadChatItems(latestMessages);
        setUser2Info(playerList.get(0).getUsername(), playerList.get(0).isOnline());
    }

    @FXML
    public void handleBackToHomeClick() {
        Platform.runLater(() -> { // Chạy trong luồng FX
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/Home.fxml"));
                Parent root = loader.load();
                HomeController homeController = loader.getController();

                int clientId = client.getID();
                ArrayList<Invitation> invitationList = socketHandle.invitations;
                ArrayList<User> playerList = socketHandle.userList;

                homeController.loadData(clientId, invitationList, playerList, "");
                homeController.loadMessage();

                // Cập nhật giai điệu và ẩn cảnh hiện tại
                Stage stage = (Stage) backToHome.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Memory Game");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Xử lý IOException
            }
        });
    }
    public void setUser2Info(String name, boolean isOnline) {
        user2name.setText(name); // Thiết lập tên người dùng

        // Cập nhật trạng thái trực tuyến
        if (isOnline) {
            user2status.setFill(Color.GREEN); // Xanh nếu trực tuyến
            user2statusLabel.setText("Online");
        } else {
            user2status.setFill(Color.GRAY); // Đỏ nếu ngoại tuyến
            user2statusLabel.setText("Offline");
        }


        // Căn chỉnh chiều dài của user2name
        user2name.setMinWidth(Label.USE_PREF_SIZE); // Sử dụng kích thước tối ưu
        user2name.setPrefWidth(Label.USE_COMPUTED_SIZE); // Tính toán kích thước
    }
    // Phương thức xử lý khi nhấn nút gửi
    @FXML
    private void handleSendClick(MouseEvent event) {
        String messageText = message.getText(); // Lấy văn bản từ TextField

        if (!messageText.isEmpty()) {
            // Xử lý gửi tin nhắn, ví dụ gửi đến máy chủ hoặc thêm vào giao diện
            String request = "send-message-to-user,text," + client.getID() + "," + selectedChatItemController.getUserID() + "," + messageText;
            try {
                socketHandle.write(request);
                String stringTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                sendMsg(messageText, stringTime);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Tin nhắn đã gửi: " + request);
            message.clear(); // Xóa TextField sau khi gửi

        }
    }

    private void sendMsg(String msgToSend, String timestamp) {
        selectedChatItemController.setLatestMess("You: " + msgToSend);
        Platform.runLater(() -> {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5, 15, 0, 100));

            Text text = new Text(msgToSend);
            text.setStyle("-fx-font-size: 16");
            TextFlow textFlow = new TextFlow(text);

            // Style cho TextFlow
            textFlow.setStyle("-fx-background-color: #0693e3; -fx-font-weight: normal; -fx-color: white; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(Color.color(1, 1, 1));

            hBox.getChildren().add(textFlow);

            HBox hBoxTime = new HBox();
            hBoxTime.setAlignment(Pos.CENTER_RIGHT);
            hBoxTime.setPadding(new Insets(0, 15, 5, 100));
            Text time = new Text(timestamp);
            time.setStyle("-fx-font-size: 12");

            hBoxTime.getChildren().add(time);

            vBoxContent.getChildren().addAll(hBox, hBoxTime);

            autoScroll();

        });
    }




    // Phương thức xử lý khi nhấn nút đính kèm
//    @FXML
//    private void handleClipClick(MouseEvent event) throws IOException {
//        FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
//        dialog.setMode(FileDialog.LOAD);
//        dialog.setVisible(true);
//        String pathfile = dialog.getDirectory()+dialog.getFile();
//        dialog.dispose();
//
//        byte[] imageBytes = readImageToByteArray(pathfile);
//        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
//
//        String request = "send-message-to-user,image," + client.getID() + "," + selectedChatItemController.getUserID() + "," + encodedImage;
//        socketHandle.write(request);
//        String stringTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//        sendImage(encodedImage, stringTime);
//        autoScroll();
//
//
//    }
//    @FXML
//    private void handleClipClick(MouseEvent event) throws IOException {
//        // Create the FileDialog
//        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
//        dialog.setMode(FileDialog.LOAD);
//        dialog.setVisible(true);
//
//        // Get the selected file path
//        String directory = dialog.getDirectory();
//        String filename = dialog.getFile();
//
//        // Dispose of the dialog after getting the selection
//        dialog.dispose();
//
//        // Check if a file was selected
//        if (filename != null) {
//            // Combine the directory and filename to get the full file path
//            String pathfile = directory + filename;
//
//            // Read the image file into a byte array
//            byte[] imageBytes = readImageToByteArray(pathfile);
//            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
//
//            // Create the request to send over the socket
//            String request = "send-message-to-user,image," + client.getID() + "," + selectedChatItemController.getUserID() + "," + encodedImage;
//            socketHandle.write(request);
//
//            // Get the current time for the message timestamp
//            String stringTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//            // Send the image along with the timestamp
//            sendImage(encodedImage, stringTime);
//
//            // Auto-scroll if needed
//            autoScroll();
//        } else {
//            // Optionally, you can show a message to the user that no file was selected
//            System.out.println("No file was selected. Message not sent.");
//        }
//    }
    @FXML
    private void handleClipClick(MouseEvent event) throws IOException {
        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");

        // Set the extension filters
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        // Show the open dialog
        File selectedFile = fileChooser.showOpenDialog(null);

        // Check if a file was selected
        if (selectedFile != null) {
            // Get the selected file path
            String pathfile = selectedFile.getAbsolutePath();

            // Read the image file into a byte array
            byte[] imageBytes = readImageToByteArray(pathfile);
            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

            // Create the request to send over the socket
            String request = "send-message-to-user,image," + client.getID() + "," + selectedChatItemController.getUserID() + "," + encodedImage;
            socketHandle.write(request);

            // Get the current time for the message timestamp
            String stringTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Send the image along with the timestamp
            sendImage(encodedImage, stringTime);

            // Auto-scroll if needed
            autoScroll();
        } else {
            // Optionally, you can show a message to the user that no file was selected
            System.out.println("No file was selected. Message not sent.");
        }
    }
//
//    // Method to check if the file is a valid image format
//    private boolean isValidImageFile(String filename) {
//        // Define accepted image formats
//        String[] validExtensions = { "png", "jpg", "jpeg", "gif" };
//
//        // Get the file extension
//        String fileExtension = getFileExtension(filename);
//
//        // Check if the extension is valid
//        for (String extension : validExtensions) {
//            if (fileExtension.equalsIgnoreCase(extension)) {
//                return true;
//            }
//        }
//        return false;
//    }

//    // Method to get the file extension from the filename
//    private String getFileExtension(String filename) {
//        int dotIndex = filename.lastIndexOf('.');
//        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
//            return filename.substring(dotIndex + 1);
//        }
//        return ""; // No extension found
//    }

    private void sendImage(String encodedImage, String timestamp) throws IOException {
        selectedChatItemController.setLatestMess("You: Send an image.");
        Platform.runLater(() -> {
            byte[] imageBytes = Base64.getDecoder().decode(encodedImage);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            Image image = new Image(bis);

            // Tạo ImageView từ image
            ImageView imageView = new ImageView(image);

            // Kiểm tra chiều cao của hình ảnh và thiết lập chiều cao
            if (image.getHeight() > 200) {
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(true); // Bảo toàn tỷ lệ hình ảnh
            } else {
                imageView.setFitHeight(image.getHeight());
                imageView.setFitWidth(image.getWidth());
            }

            HBox hBox = new HBox();
            hBox.setPadding(new Insets(5, 15, 5, 100));
            hBox.getChildren().add(imageView);
            hBox.setAlignment(Pos.CENTER_RIGHT);

            HBox hBoxTime = new HBox();
            hBoxTime.setAlignment(Pos.CENTER_RIGHT);
            hBoxTime.setPadding(new Insets(0, 15, 5, 100));
            Text time = new Text(timestamp);
            time.setStyle("-fx-font-size: 12");

            hBoxTime.getChildren().add(time);

            vBoxContent.getChildren().add(hBox);
            vBoxContent.getChildren().add(hBoxTime);
            autoScroll();

        });

    }
    public static byte[] readImageToByteArray(String imagePath) throws IOException {
        File file = new File(imagePath);
        byte[] imageBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(imageBytes);
        }
        return imageBytes;
    }

    public void receiveMsg(String message, String timestamp)
    {
        User u = new User();
        User user = u.getUserFromID(playerList, selectedChatItemController.getUserID());
        selectedChatItemController.setLatestMess( user.getUsername() + ": " + message);
        Platform.runLater(() -> {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,100,5,10));

            Text text = new Text(message);
            TextFlow textFlow = new TextFlow(text);
            text.setStyle("-fx-font-size: 16");
            textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: normal; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0,0,0));

            hBox.getChildren().add(textFlow);
            HBox hBoxTime = new HBox();
            hBoxTime.setAlignment(Pos.CENTER_LEFT);
            hBoxTime.setPadding(new Insets(0, 100, 5, 10));

            Text time = new Text(timestamp);
            time.setStyle("-fx-font-size: 12");

            hBoxTime.getChildren().add(time);
            vBoxContent.getChildren().add(hBox);
            vBoxContent.getChildren().add(hBoxTime);
            autoScroll();
        });
    }

    public void receiveImage(String encodedImage, String timestamp) {
        User u = new User();
        User user = u.getUserFromID(playerList, selectedChatItemController.getUserID());
        selectedChatItemController.setLatestMess( user.getUsername() + ": send an image.");
        Platform.runLater(() -> {
            byte[] imageBytes = Base64.getDecoder().decode(encodedImage);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            Image image = new Image(bis);

            ImageView imageView = new ImageView(image);
            if (image.getHeight() > 200) {
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(true); // Bảo toàn tỷ lệ hình ảnh
            } else {
                imageView.setFitHeight(image.getHeight());
                imageView.setFitWidth(image.getWidth());
            }

            HBox hBox = new HBox();
            hBox.setPadding(new Insets(5, 100, 5, 10));
            hBox.getChildren().add(imageView);
            hBox.setAlignment(Pos.CENTER_LEFT);

            HBox hBoxTime = new HBox();
            hBoxTime.setAlignment(Pos.CENTER_LEFT);
            hBoxTime.setPadding(new Insets(0, 100, 5, 10));
            Text time = new Text(timestamp);
            time.setStyle("-fx-font-size: 12");

            hBoxTime.getChildren().add(time);

            vBoxContent.getChildren().add(hBox);
            vBoxContent.getChildren().add(hBoxTime);
            autoScroll();
        });
    }
    // tu dong cuon xuong cuoi
    public void autoScroll() {
        // Cuộn xuống cuối sau khi thêm nội dung mới
        vBoxContent.heightProperty().addListener((observable, oldValue, newValue) -> {
            scrollPane.setVvalue(1.0); // Tự động cuộn xuống cuối khi chiều cao VBox thay đổi
        });


    }
    public void updateChatting(String[] messageSplit) throws IOException {
        Platform.runLater(() -> {
            try {
                for (int i = 3; i < messageSplit.length; i += 6) {
                    if (i + 5 < messageSplit.length) {
                        Message message = createMessItemFromMessage(messageSplit, i);
                        String time = message.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        // Display the message based on its type and sender
                        if (message.getType().equals("text")) {
                            if (message.getSenderId() == client.getID()) {
                                sendMsg(message.getContent(), time);
                            } else if (message.getSenderId() == selectedChatItemController.getUserID()) {
                                receiveMsg(message.getContent(), time);
                            }
                        } else if (message.getType().equals("image")) {
                            if (message.getSenderId() == client.getID()) {
                                sendImage(message.getContent(), time);
                            } else if (message.getSenderId() == selectedChatItemController.getUserID()) {
                                receiveImage(message.getContent(), time);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Message createMessItemFromMessage(String[] messageSplit, int index) {
        return new Message(
                Integer.parseInt(messageSplit[index]),
                Integer.parseInt(messageSplit[index + 1]),
                Integer.parseInt(messageSplit[index + 2]),
                messageSplit[index + 3],
                LocalDateTime.parse(messageSplit[index + 4]),
                messageSplit[index + 5]
        );
    }
    public void updateReceiveChat(String type, int senderId, String messageContent, String stringTime)
    {
        Platform.runLater(() -> {
            if (type.equals("text") && senderId == selectedChatItemController.getUserID()) {
                receiveMsg(messageContent, stringTime);
            } else if (type.equals("image") && senderId == selectedChatItemController.getUserID()) {
                receiveImage(messageContent, stringTime);
            }
        });

    }

}

