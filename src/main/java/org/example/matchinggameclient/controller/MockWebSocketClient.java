package org.example.matchinggameclient.controller;

import org.example.matchinggameclient.model.MatchHistory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockWebSocketClient {
    public List<MatchHistory> fetchMatchHistory() {
        // Giả lập dữ liệu từ server
        List<MatchHistory> matchHistories = new ArrayList<>();
        matchHistories.add(new MatchHistory(1, 1001L, 2001L, "Win", 50, LocalDateTime.now()));
        matchHistories.add(new MatchHistory(2, 1002L, 2002L, "Lose", 20, LocalDateTime.now().minusDays(1)));
        matchHistories.add(new MatchHistory(3, 1003L, 2003L, "Win", 75, LocalDateTime.now().minusWeeks(1)));
        matchHistories.add(new MatchHistory(4, 1004L, 2004L, "Draw", 35, LocalDateTime.now().minusWeeks(2)));
        matchHistories.add(new MatchHistory(5, 1005L, 2005L, "Lose", 10, LocalDateTime.now().minusMonths(1)));
        matchHistories.add(new MatchHistory(6, 1006L, 2006L, "Win", 80, LocalDateTime.now().minusMonths(1).minusDays(2)));
        matchHistories.add(new MatchHistory(7, 1007L, 2007L, "Draw", 45, LocalDateTime.now().minusDays(3)));
        matchHistories.add(new MatchHistory(8, 1009L, 2009L, "Lose", 5, LocalDateTime.now().minusDays(10)));

        return matchHistories;
    }
}
