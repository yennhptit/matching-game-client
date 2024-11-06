package org.example.matchinggameclient.controller;

import org.example.matchinggameclient.model.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockWebSocketClient1 {
    private List<Card> cardData;
    private List<String> players;
    private int player1Score;
    private int player2Score;

    public MockWebSocketClient1() {
        this.cardData = generateCardData();
        this.players = generatePlayers();
        this.player1Score = 0;
        this.player2Score = 0;
    }

    private List<Card> generateCardData() {
        List<Card> cards = new ArrayList<>();

        cards.add(new Card(1, "Card 1", "https://i.pinimg.com/564x/ea/8b/da/ea8bda52376ec691c64d8be968f095b6.jpg"));
        cards.add(new Card(1, "Card 1", "https://i.pinimg.com/564x/ea/8b/da/ea8bda52376ec691c64d8be968f095b6.jpg"));

        cards.add(new Card(2, "Card 2", "https://i.pinimg.com/564x/27/56/cc/2756cc330ffa7919f9c57367216bcd67.jpg"));
        cards.add(new Card(2, "Card 2", "https://i.pinimg.com/564x/27/56/cc/2756cc330ffa7919f9c57367216bcd67.jpg"));

        cards.add(new Card(3, "Card 3", "https://i.pinimg.com/564x/46/06/f5/4606f52c8e537d58f22e950e663cde68.jpg"));
        cards.add(new Card(3, "Card 3", "https://i.pinimg.com/564x/46/06/f5/4606f52c8e537d58f22e950e663cde68.jpg"));

        cards.add(new Card(4, "Card 4", "https://i.pinimg.com/564x/23/f0/38/23f038cc913bb02b9b58bc7d8418dd6d.jpg"));
        cards.add(new Card(4, "Card 4", "https://i.pinimg.com/564x/23/f0/38/23f038cc913bb02b9b58bc7d8418dd6d.jpg"));

        cards.add(new Card(5, "Card 5", "https://i.pinimg.com/564x/30/69/1d/30691d2c1485e2c95540589722fee70b.jpg"));
        cards.add(new Card(5, "Card 5", "https://i.pinimg.com/564x/30/69/1d/30691d2c1485e2c95540589722fee70b.jpg"));

        cards.add(new Card(6, "Card 6", "https://i.pinimg.com/564x/2d/0e/e1/2d0ee150ae5d9aa1d919f40504a35a1e.jpg"));
        cards.add(new Card(6, "Card 6", "https://i.pinimg.com/564x/2d/0e/e1/2d0ee150ae5d9aa1d919f40504a35a1e.jpg"));

        cards.add(new Card(7, "Card 7", "https://i.pinimg.com/564x/31/8d/ef/318defe2c77b96e90c458c6f527c857a.jpg"));
        cards.add(new Card(7, "Card 7", "https://i.pinimg.com/564x/31/8d/ef/318defe2c77b96e90c458c6f527c857a.jpg"));

        cards.add(new Card(8, "Card 8", "https://i.pinimg.com/736x/3e/3f/94/3e3f94ef91a8bac351a76bce2b75a772.jpg"));
        cards.add(new Card(8, "Card 8", "https://i.pinimg.com/736x/3e/3f/94/3e3f94ef91a8bac351a76bce2b75a772.jpg"));

        cards.add(new Card(9, "Card 9", "https://i.pinimg.com/564x/61/03/b6/6103b6e97a6adddd6366e92113311dc7.jpg"));
        cards.add(new Card(9, "Card 9", "https://i.pinimg.com/564x/61/03/b6/6103b6e97a6adddd6366e92113311dc7.jpg"));

        cards.add(new Card(10, "Card 10", "https://i.pinimg.com/564x/75/18/45/7518455d7e0310c613bb54879fd7f701.jpg"));
        cards.add(new Card(10, "Card 10", "https://i.pinimg.com/564x/75/18/45/7518455d7e0310c613bb54879fd7f701.jpg"));

        return cards;
    }


    private List<String> generatePlayers() {
        List<String> players = new ArrayList<>();
        players.add("Player1");
        players.add("Player2");
        return players;
    }

    public List<Card> getCardData() {
        return cardData;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void updatePlayerScore(String player, int score) {
        if (player.equals("Player1")) {
            player1Score += score;
        } else {
            player2Score += score;
        }
    }

    public int getPlayerScore(String player) {
        return player.equals("Player1") ? player1Score : player2Score;
    }

    public String checkGameStatus() {
        if (player1Score >= 100) return "Player1 Wins!";
        if (player2Score >= 100) return "Player2 Wins!";
        return player1Score > player2Score ? "Player1 is leading" : "Player2 is leading";
    }
}
