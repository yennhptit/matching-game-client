package org.example.matchinggameclient.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class MatchHistory {
    private Integer userId;
    private Long matchId;
    private String result;
    private Integer pointsEarned;
    private LocalDateTime createdAt;  // Thay đổi thành kiểu dữ liệu LocalDateTime

    public MatchHistory() {}

    public MatchHistory(Integer userId, Long matchId, String result, Integer pointsEarned, LocalDateTime createdAt) {
        this.userId = userId;
        this.matchId = matchId;
        this.result = result;
        this.pointsEarned = pointsEarned;
        this.createdAt = createdAt;
    }

    public MatchHistory(Integer userId, Long matchId, String result, Integer pointsEarned) {
        this.userId = userId;
        this.matchId = matchId;
        this.result = result;
        this.pointsEarned = pointsEarned;
        this.createdAt = LocalDateTime.now(); // Gán thời gian hiện tại
    }

    // Getters and Setters

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchHistory)) return false;
        MatchHistory that = (MatchHistory) o;
        return
                Objects.equals(userId, that.userId) &&
                Objects.equals(matchId, that.matchId) &&
                Objects.equals(result, that.result) &&
                Objects.equals(pointsEarned, that.pointsEarned) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash( userId, matchId, result, pointsEarned, createdAt);
    }
}