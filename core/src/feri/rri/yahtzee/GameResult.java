package feri.rri.yahtzee;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GameResult {
    private int score;
    private String timestamp;
    private String playerName;

    public GameResult(int score, String playerName) {
        this.score = score;
        this.playerName = playerName;
        this.timestamp = getCurrentTimestamp();
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
