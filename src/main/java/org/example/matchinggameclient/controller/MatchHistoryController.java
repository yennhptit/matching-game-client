package org.example.matchinggameclient.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.example.matchinggameclient.model.MatchHistory;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class MatchHistoryController implements Initializable {
    @FXML
    private AnchorPane rootPane;  // Để đặt kích thước màn hình
    @FXML
    private TableView<MatchHistory> matchHistoryTable;
    @FXML
    private TableColumn<MatchHistory, Integer> idColumn;
    @FXML
    private TableColumn<MatchHistory, Long> userIdColumn;
    @FXML
    private TableColumn<MatchHistory, Long> matchIdColumn; // Cột Match ID
    @FXML
    private TableColumn<MatchHistory, String> resultColumn; // Cột Kết quả
    @FXML
    private TableColumn<MatchHistory, Integer> pointsEarnedColumn; // Cột Điểm kiếm được
    @FXML
    private TableColumn<MatchHistory, String> createdAtColumn; // Cột Thời gian tạo
    @FXML
    private TextField searchField;  // Trường tìm kiếm
    @FXML
    private ComboBox<String> filterByComboBox;  // ComboBox để chọn bộ lọc
    @FXML
    private Button previousPageButton; // Nút quay lại
    @FXML
    private Button nextPageButton; // Nút tiếp theo
    @FXML
    private Label userNameLabel, userStarsLabel; // Thông tin người dùng
    @FXML
    private Button homeButton, logoutButton; // Nút điều hướng
    @FXML
    private HBox paginationBox;
    private ObservableList<MatchHistory> data;
    private ObservableList<MatchHistory> originalData; // Biến lưu trữ dữ liệu gốc
    private MockWebSocketClient webSocketClient;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private int currentPage = 0;
    private final int rowsPerPage = 20; // Giá trị mặc định
    private void updatePagination() {
        paginationBox.getChildren().clear(); // Xóa các nút cũ
        int totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);

        for (int i = 0; i < totalPages; i++) {
            Button pageButton = new Button(String.valueOf(i + 1));
            final int pageIndex = i; // Biến cục bộ để sử dụng trong lambda
            pageButton.setOnAction(event -> {
                currentPage = pageIndex;
                updateTableRows();
            });
            paginationBox.getChildren().add(pageButton);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webSocketClient = new MockWebSocketClient();
        List<MatchHistory> matchHistoryList = webSocketClient.fetchMatchHistory();
        originalData = FXCollections.observableArrayList(matchHistoryList); // Lưu trữ dữ liệu gốc
        data = FXCollections.observableArrayList(matchHistoryList);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        matchIdColumn.setCellValueFactory(new PropertyValueFactory<>("matchId"));
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        pointsEarnedColumn.setCellValueFactory(new PropertyValueFactory<>("pointsEarned"));
        createdAtColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCreatedAt().format(dateTimeFormatter)));
        updateTableRows();
        matchHistoryTable.setRowFactory(tv -> new TableRow<MatchHistory>() {
            @Override
            protected void updateItem(MatchHistory item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    switch (item.getResult()) {
                        case "Win":
                            setStyle("-fx-background-color: #CCFFCC;");
                            break;
                        case "Lose":
                            setStyle("-fx-background-color: #D3D3D3;");
                            break;
                        case "Draw":
                            setStyle("-fx-background-color: #FFFFCC;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });

        filterByComboBox.getItems().addAll("All", "ID", "Time", "Player", "Result");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> performSearch());

//        previousPageButton.setOnAction(event -> {
//            if (currentPage > 0) {
//                currentPage--;
//                updateTableRows();
//            }
//        });
//
//        nextPageButton.setOnAction(event -> {
//            if ((currentPage + 1) * rowsPerPage < data.size()) {
//                currentPage++;
//                updateTableRows();
//            }
//        });

        updateTableRows();
    }

    private void performSearch() {
        String query = searchField.getText().toLowerCase();
        String filterBy = filterByComboBox.getValue();
        ObservableList<MatchHistory> filteredData = FXCollections.observableArrayList();

        for (MatchHistory match : originalData) { // Sử dụng originalData để tìm kiếm
            boolean matches = false;
            if (filterBy == null || filterBy.equals("All")) {
                // Tìm kiếm trên tất cả các cột
                matches = String.valueOf(match.getId()).toLowerCase().contains(query) ||
                        String.valueOf(match.getUserId()).toLowerCase().contains(query) ||
                        String.valueOf(match.getMatchId()).toString().toLowerCase().contains(query) ||
                        match.getResult().toLowerCase().contains(query) ||
                        match.getCreatedAt().format(dateTimeFormatter).toLowerCase().contains(query);
            } else if (filterBy != null) {
                switch (filterBy) {
                    case "ID":
                        matches = String.valueOf(match.getId()).toLowerCase().contains(query);
                        break;
                    case "Time":
                        matches = match.getCreatedAt().format(dateTimeFormatter).toLowerCase().contains(query);
                        break;
                    case "Player":
                        matches = String.valueOf(match.getUserId()).toLowerCase().contains(query);
                        break;
                    case "Result":
                        matches = match.getResult().toLowerCase().contains(query);
                        break;
                }
            }
            if (matches) {
                filteredData.add(match);
            }
            updatePagination();
        }

        data = filteredData; // Cập nhật data với filteredData
        currentPage = 0; // Đặt lại trang hiện tại về 0
        updateTableRows(); // Cập nhật hàng trong bảng để phản ánh dữ liệu tìm kiếm
    }

    private static final int ROW_HEIGHT = 24; // Chiều cao của mỗi hàng
    private static final int MAX_ROWS = 20; // Số dòng tối đa

    private void updateTableRows() {
        int totalDataSize = data.size();
        int fromIndex = currentPage * rowsPerPage;

        // Nếu fromIndex vượt quá tổng số dữ liệu, không hiển thị hàng nào
        if (fromIndex >= totalDataSize) {
            matchHistoryTable.setItems(FXCollections.observableArrayList()); // Bảng rỗng
            return;
        }

        // Tính toIndex
        int toIndex = Math.min(fromIndex + rowsPerPage, totalDataSize);

        // Lấy sublist với dữ liệu thực tế
        ObservableList<MatchHistory> pagedData = FXCollections.observableArrayList(data.subList(fromIndex, toIndex));

        // Đặt dữ liệu cho bảng
        matchHistoryTable.setItems(pagedData);

        // Điều chỉnh chiều cao bảng cho 20 hàng
        int numberOfRowsToDisplay = Math.min(totalDataSize - fromIndex, MAX_ROWS); // Số dòng sẽ hiển thị
        matchHistoryTable.setPrefHeight(numberOfRowsToDisplay * 30 + 25); // 75 là chiều cao bổ sung cho khoảng cách và các điều khiển
        updatePagination();
    }
}
