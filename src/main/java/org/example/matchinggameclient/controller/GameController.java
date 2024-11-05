package org.example.matchinggameclient.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.matchinggameclient.model.Card;
import org.example.matchinggameclient.model.Invitation;
import org.example.matchinggameclient.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameController {

    @FXML
    private Label gameLabel;
    private Timeline timer = new Timeline();
    @FXML
    private Label usernameLabel, starsLabel, timeLabel, scoreLabel, pairsFlippedLabel, opponentScoreLabel, opponentLabel;
    @FXML
    private Button exitButton;
    @FXML
    private GridPane gameGrid;

    private List<Card> cards = new ArrayList<>();
    private int timeLeft = 60; // 5 minutes in seconds
    private int score = 0;
    private int pairsFlipped = 0;
    private SocketHandle socketHandle;
    private Button firstCard = null;
    private Button secondCard = null;
//    @FXML
//    private Text statusText;
    private User client;
    private Long matchId;
    private Integer opponentId;

    public void initialize() {
        // Thiết lập socket handle cho controller
        socketHandle = SocketHandle.getInstance();
        socketHandle.setGameController(this);
        exitButton.setOnAction(event -> {
            try {
                showExitGameAlert();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        if (gameGrid == null) {
            System.out.println("gameGrid chưa được khởi tạo!");
        }
        updateTime();
    }

    public void loadData(User client, Long matchId, Integer opponentId, String opponentUsername, List<Card> cards) {
        this.matchId = matchId;
        this.client = client;
        this.cards = cards;
        this.opponentId = opponentId;
        // Cập nhật giao diện trò chơi
        Platform.runLater(() -> {
            scoreLabel.setText("Score: " + score);
            pairsFlippedLabel.setText("Pairs Flipped: " + pairsFlipped);
            usernameLabel.setText("Player: " + client.getUsername());
            opponentLabel.setText("Opponent: " + opponentUsername);
//            starsLabel.setText("Stars: 5");
            setupGameBoard(cards); // Gọi phương thức thiết lập bảng trò chơi với danh sách lá bài đã tải

        });
        // Thiết lập ban đầu cho các nhãn và giá trị

        // Thông báo cho console
        System.out.println("Loaded data for match ID: " + matchId);
    }

    private void updateTime() {

        this.timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            if (timeLeft > 0) {
                timeLeft--;
                int minutes = timeLeft / 60;
                int seconds = timeLeft % 60;
                timeLabel.setText(String.format("Time Left: %d:%02d", minutes, seconds));
            } else {
                this.timer.stop();
                try {
                    sendMessageEndGame();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }));
        this.timer.setCycleCount(Timeline.INDEFINITE);
        this.timer.play();
    }

    private void setupGameBoard(List<Card> cards) {
        gameGrid.getChildren().clear();
        // Shuffle the cards
        Collections.shuffle(cards);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                Button card = new Button();
                card.getStyleClass().add("button-card");
                card.setUserData(cards.get(i*5+j)); // Set the card data
                card.setOnAction(e -> handleCardClick(card));
                gameGrid.add(card, j, i);
            }
        }
    }

    private void handleCardClick(Button card) {
        if (firstCard == null) {
            firstCard = card;
            showCard(card);
        } else if (secondCard == null && card != firstCard) {
            secondCard = card;
            showCard(card);
            checkForMatch();
        }
    }

//    private void showCard(Button card) {
//        Card cardData = (Card) card.getUserData();
//        card.setText(String.valueOf(cardData.getId())); // Show card id
//        card.setDisable(true); // Disable card to prevent re-clicking
//    }
    private void showCard(Button card) {
        Card cardData = (Card) card.getUserData();
        ImageView imageView = new ImageView(new Image(cardData.getImage()));
        imageView.setFitWidth(96); // Điều chỉnh kích thước ảnh cho phù hợp với thẻ
        imageView.setFitHeight(144);
        card.setGraphic(imageView);
        card.setDisable(true);
    }

    private void checkForMatch() {
        if (firstCard != null && secondCard != null) {
            Card firstCardData = (Card) firstCard.getUserData();
            Card secondCardData = (Card) secondCard.getUserData();
            if (firstCardData.getId() == secondCardData.getId()) {
                String request = "card-flip," + matchId + "," + client.getID() + "," + opponentId + "," + "true";
                try {
                    socketHandle.write(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // If both cards match
                score += 1; // Increase score for a match
                pairsFlipped++;
                scoreLabel.setText("Score: " + score);
                pairsFlippedLabel.setText("Pairs Flipped: " + pairsFlipped);

                // Hide the second card after a short delay
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
                    firstCard.setVisible(false);
                    secondCard.setVisible(false);
                    firstCard = null;
                    secondCard = null;
                }));
                timeline.play();
            } else {
                // If cards do not match, hide them after a delay
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
                    hideCards(firstCard, secondCard);
                    firstCard = null;
                    secondCard = null;
                }));
                timeline.play();
            }
        }
    }

//    private void hideCards(Button firstCard, Button secondCard) {
//        if (firstCard != null) {
//            firstCard.setText(""); // Reset the text to hide the card
//            firstCard.setDisable(false); // Enable card for future clicks
//        }
//        if (secondCard != null) {
//            secondCard.setText(""); // Reset the text to hide the card
//            secondCard.setDisable(false); // Enable card for future clicks
//        }
//    }
    private void hideCards(Button firstCard, Button secondCard) {
        if (firstCard != null) {
            firstCard.setGraphic(null); // Ẩn hình ảnh thẻ
            firstCard.setDisable(false); // Kích hoạt lại thẻ
        }
        if (secondCard != null) {
            secondCard.setGraphic(null); // Ẩn hình ảnh thẻ
            secondCard.setDisable(false); // Kích hoạt lại thẻ
        }
    }

    @FXML
    private void handleHomeAction() {
        System.out.println("Home button clicked");
    }

    public void setGameLabelText(String s) {
        this.gameLabel.setText(s);
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public Timeline getTimer() {
        return timer;
    }

    public void setTimer(Timeline timer) {
        this.timer = timer;
    }

    public void gameToHome(int clientId, ArrayList<Invitation> invitationList, ArrayList<User> playerList, String chatServerContent){
        Platform.runLater(() -> { // Chạy trong luồng FX
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameclient/Home.fxml"));
                Parent root = loader.load();
                HomeController homeController = loader.getController();

                homeController.loadData(clientId, invitationList, playerList, chatServerContent);

                // Cập nhật giai điệu và ẩn cảnh hiện tại
                Stage stage = (Stage) exitButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Memory Game");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Xử lý IOException
            }
        });
    }

    public void updateOpponentPoint(){
        String[] spliLabel = opponentScoreLabel.getText().split(" ");
        int opponentPoint = Integer.parseInt(spliLabel[1]) + 1;
        Platform.runLater(() -> {
            opponentScoreLabel.setText(String.valueOf("Score: " + opponentPoint));
        });
    }

    public void checkFinalScore(String winnerId) {
        String result;

        if (winnerId.equalsIgnoreCase("null")) {
            result = "Draw!";
        } else if (Integer.parseInt(winnerId) == client.getID()) {
            result = "You Wins!";
        } else {
            result = opponentLabel.getText().split(" ")[1] + " Wins!";
        }
        showEndGameAlert(result);
    }

    public void showEndGameAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.setOnHidden(event -> {
                System.out.println("Người dùng đã nhấn OK hoặc đóng cửa sổ cảnh báo.");
                socketHandle.request = "game";
                try {
                    socketHandle.write("get-rank-charts");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            alert.showAndWait();
        });
    }

    public void showExitGameAlert() throws IOException {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exit Game");
            alert.setHeaderText(null);
            alert.setContentText("Xac nhan thoat!!");
            alert.setOnHidden(event -> {
                try {
                    socketHandle.write("end-match-exit," + matchId + "," + client.getID() + "," + opponentId + "," + opponentId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            alert.showAndWait();
        });
    }

    private void sendMessageEndGame() throws IOException {
        socketHandle.write("end-game," + matchId + "," + client.getID() + "," + opponentId);
    }
}
