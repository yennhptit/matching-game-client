//package org.example.matchinggameclient.home;
//
//import javafx.application.Platform;
//import org.example.matchinggameclient.controller.MessageHandler;
//import org.example.matchinggameclient.model.User;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//
//public class HomeSocket implements MessageHandler {
//    private int clientId;
//    private HomeView homeView;
//    private Socket socket;
//    private BufferedWriter outputWriter;
//    private BufferedReader inputReader;
//
//    public HomeSocket(int clientId, HomeView homeView) {
//        this.clientId = clientId;
//        this.homeView = homeView;
//        try {
//            // Tạo socket và kết nối đến server
//            socket = new Socket("127.0.0.1", 7777);
//            outputWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            // Gọi phương thức để lấy bảng xếp hạng khi khởi tạo
//            fetchRankCharts();
//            String message;
//            while ((message = inputReader.readLine()) != null) {
//                System.out.println("message: " + message);
//                onMessageReceived(message); // Notify the handler
//            }
//        } catch (IOException e) {
//            System.err.println("IOException while connecting: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public void fetchRankCharts() {
//        String request = "get-rank-charts," + "test"; // Chuỗi yêu cầu
//        try {
//            // Gửi yêu cầu đến server
//            outputWriter.write(request);
//            outputWriter.newLine();
//            outputWriter.flush();
//            System.out.println("gưi thanh cong");
////            // Đọc phản hồi từ server
////            String response = inputReader.readLine();
////            onMessageReceived(response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onMessageReceived(String message) {
//
//        // Print the entire incoming message for debugging
//        System.out.println("Received message: " + message);
//
//        // Split the incoming message into parts
//        String[] data = message.split(",");
//
//        // Ensure there is at least one part to avoid ArrayIndexOutOfBoundsException
//        if (data.length == 0) {
//            System.err.println("Invalid response: empty message.");
//            return; // Exit if the response is empty
//        }
//        System.out.println(data[0]);
//
//        // Handle specific message types based on the first element
//        switch (data[0]) {
//
//            case "return-get-rank-charts":
//                // Process rank charts
//                ArrayList<User> userList = new ArrayList<>();
//                User client = new User();
//                for (int i = 1; i < data.length; i += 10) { // Move in steps of 10 for each user
//                    if (i + 9 < data.length) { // Ensure there are enough parts to create a User
//                        int id = Integer.parseInt(data[i]);
//                        String username = data[i + 1];
//                        String password = data[i + 2]; // 'null' can be handled if necessary
//                        int numberOfGame = Integer.parseInt(data[i + 3]);
//                        int numberOfWin = Integer.parseInt(data[i + 4]);
//                        int numberOfDraw = Integer.parseInt(data[i + 5]);
//                        boolean isOnline = Boolean.parseBoolean(data[i + 6]);
//                        boolean isPlaying = Boolean.parseBoolean(data[i + 7]);
//                        int star = Integer.parseInt(data[i + 8]);
//                        int rank = Integer.parseInt(data[i + 9]);
//
//                        // Create a User object and add it to the list
//                        User user = new User(id, username, password, numberOfGame, numberOfWin, numberOfDraw, isOnline, isPlaying, star, rank);
//                        if (user.getID() == clientId)
//                            client = user;
//                        System.out.println(user.toString());
//                        userList.add(user);
//                    } else {
//                        System.err.println("Insufficient data for a User object at index: " + i);
//                    }
//                }
//                // Update the HomeView with the new user list
//                updateHomeView(userList);
//                break;
//
//            case "wrong-user":
//                // Handle invalid username/password
//                System.err.println("Invalid username or password!");
//                // Optionally, update the UI to notify the user
//                break;
//
//            case "login-success":
//                // Handle successful login
//                System.out.println("Login successful!");
//                // Parse additional user data as needed
//                break;
//
//            case "duplicate-login":
//                // Handle duplicate login attempts
//                System.err.println("User is already logged in.");
//                // Optionally, update the UI to notify the user
//                break;
//
//            default:
//                System.err.println("Unknown message type: " + data[0]);
//                break;
//        }
//    }
//
//    private User getUserFromString(String userData) {
//        String[] parts = userData.split(";"); // Giả sử dữ liệu người dùng được phân tách bằng dấu ;
//        int id = Integer.parseInt(parts[0]);
//        String name = parts[1];
//        int star = Integer.parseInt(parts[2]);
//        boolean online = Boolean.parseBoolean(parts[3]);
//        return new User(id, name, star, online);
//    }
//
//    public void updateHomeView(ArrayList<User> userList) {
//        // Cập nhật giao diện HomeView với danh sách userList
//        System.out.println("Updating HomeView with new user list:");
//        homeView.updateUserList(userList); // Cần định nghĩa phương thức này trong HomeView
//        for (User u : userList) {
//            System.out.println(u);
//        }
//    }
//
//    // Đừng quên đóng socket và stream khi không còn sử dụng
//    public void close() {
//        try {
//            if (outputWriter != null) {
//                outputWriter.close();
//            }
//            if (inputReader != null) {
//                inputReader.close();
//            }
//            if (socket != null) {
//                socket.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
