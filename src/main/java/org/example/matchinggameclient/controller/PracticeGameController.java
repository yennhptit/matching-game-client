package org.example.matchinggameclient.controller;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.matchinggameclient.model.Card;
import org.example.matchinggameclient.model.Invitation;
import org.example.matchinggameclient.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PracticeGameController {
    @FXML
    private Label usernameLabel, starsLabel, timeLabel, scoreLabel, pairsFlippedLabel;
    @FXML
    private Button exitButton;
    public ArrayList<User> playerList;
    private User client;
    @FXML
    private Label clientInfoLabel;
    @FXML
    private GridPane gameGrid;
    private SocketHandle socketHandle;
    @FXML
    private Label opponentLabel, opponentScoreLabel;
    private int timeLeft = 20; // Thay đổi theo thời gian bạn muốn
    private int score = 0;
    private int pairsFlipped = 0;

    private List<Card> cards;
    private Button firstCard = null;
    private Button secondCard = null;
    private MockWebSocketClient1 mockClient ;
    private Timeline timer;

    public void initialize() {
        if (client != null) {
            clientInfoLabel.setText(String.format("Player: %s    Rank: #%03d    Stars: %d",
                    client.getUsername(), client.getRank(), client.getStar()));
        } else {
            clientInfoLabel.setText("Player: Unknown");
        }

        mockClient = new MockWebSocketClient1();
        setupGameBoard();
        startTimer();
        createClockBlinkEffect();
        socketHandle = SocketHandle.getInstance();
        socketHandle.setPracticeGameController(this);
        exitButton.setOnAction(actionEvent -> {
            try {
                socketHandle.write("practice-to-home");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
    public void setClient(User client) {
        this.client = client;
        if (client != null) {
            clientInfoLabel.setText(String.format("Player: %s    Rank: #%03d    Stars: %d",
                    client.getUsername(), client.getRank(), client.getStar()));
        } else {
            clientInfoLabel.setText("Player: Unknown");
        }
    }
    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (timeLeft > 0) {
                timeLeft--;
                int minutes = timeLeft / 60;
                int seconds = timeLeft % 60;
                timeLabel.setText(String.format("Time Left: %d:%02d", minutes, seconds));
            } else {
                endGame("Thời gian đã hết! Bạn thua!");
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void setupGameBoard() {
        gameGrid.getChildren().clear();
        cards = mockClient.getCardData();
        Collections.shuffle(cards);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                Button card = new Button();
                card.getStyleClass().add("button-card");
                card.setUserData(cards.get(i * 5 + j));
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

    private void checkForMatch() {
        if (firstCard != null && secondCard != null) {
            Card firstCardData = (Card) firstCard.getUserData();
            Card secondCardData = (Card) secondCard.getUserData();
            if (firstCardData.getId() == secondCardData.getId()) {
                // Trường hợp hai thẻ khớp
                score += 10;
                pairsFlipped++;
                scoreLabel.setText("Score: " + score);
                pairsFlippedLabel.setText("Pairs Flipped: " + pairsFlipped);

                // Ẩn thẻ sau khi khớp
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
                    firstCard.setVisible(false);
                    secondCard.setVisible(false);
                    firstCard = null;
                    secondCard = null;
                    checkGameStatus();
                }));
                timeline.play();
            } else {
                // Trường hợp không khớp, ẩn thẻ sau một khoảng thời gian
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
                    hideCards(firstCard, secondCard);
                }));
                timeline.setOnFinished(e -> {
                    firstCard = null;
                    secondCard = null;
                });
                timeline.play();
            }
        }
    }
    public Stage getStage()
    {
        return (Stage) clientInfoLabel.getScene().getWindow();
    }
    private void setClient(int id)
    {
        for(User u : playerList)
        {
            if(id == u.getID())
            {
                this.client = u;
            }
        }
        clientInfoLabel.setText(String.format("Player: %s    Rank: #%03d    Stars: %d",
                client.getUsername(), client.getRank(), client.getStar()));
    }

    private void resetSelectedCards() {
        firstCard = null;
        secondCard = null;
    }

    private void showCard(Button card) {
        Card cardData = (Card) card.getUserData();
        ImageView imageView = new ImageView(new Image(cardData.getImage()));
        imageView.setFitWidth(96); // Điều chỉnh kích thước ảnh cho phù hợp với thẻ
        imageView.setFitHeight(144);
        card.setGraphic(imageView);
        card.setDisable(true);
    }

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


    private boolean isGameWon() {
        return pairsFlipped == (cards.size() / 2);
    }

    private void checkGameStatus() {
        if (isGameWon()) {
            endGame("Chúc mừng! Bạn đã thắng!");
        }
    }

    private void endGame(String message) {
        timer.stop();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();

            // Hỏi người chơi có muốn chơi lại không
            Alert replayAlert = new Alert(Alert.AlertType.CONFIRMATION);
            replayAlert.setTitle("Play Again");
            replayAlert.setHeaderText("Bạn có muốn chơi lại không?");
            replayAlert.setContentText("Chọn 'Có' để chơi lại, 'Không' để thoát.");
            replayAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    resetGame();
                } else {
                    try {
                        socketHandle.write("practice-to-home");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            });
        });
    }

    private void resetGame() {
        timeLeft = 20; // Reset thời gian
        score = 0;
        pairsFlipped = 0;
        scoreLabel.setText("Score: " + score);
        pairsFlippedLabel.setText("Pairs Flipped: " + pairsFlipped);
        setupGameBoard();
        startTimer();
    }

    @FXML
    public void practiceToHome(int clientId, ArrayList<Invitation> invitationList, ArrayList<User> playerList, String chatServerContent) {
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

    @FXML
    private Label clockIcon;

    private void createClockBlinkEffect() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), clockIcon);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }
}
