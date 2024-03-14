package pt.ipbeja.po2.sokoban2023.model;

/**
 * Represents a score in a game.
 *
 * @author Tiago Pacheco e Gabriel Gomes
 * Nº: 20296 and 19932
 * @version 2023/06/14
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
public class Score {
    String playerName, levelName;
    int score;

    /**
     * Constructs a Score object with the specified player name, level name, and score.
     *
     * @param playerName the name of the player
     * @param levelName  the name of the level
     * @param score      the score achieved
     */
    public Score(String playerName, String levelName, int score) {
        this.playerName = playerName;
        this.levelName = levelName;
        this.score = score;
    }

    /**
     * @return playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return levelName
     */
    public String getLevelName() {
        return levelName;
    }

    /**
     * @return score
     */
    public int getScore() {
        return score;
    }

}

