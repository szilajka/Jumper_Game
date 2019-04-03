package jumper.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

/**
 * Model class of the {@link jumper.controllers.ScoreboardController}.
 */
public class ScoreBoard {

    /**
     * Current row number in the table.
     */
    private SimpleStringProperty rowNumber;

    /**
     * Username in the table.
     */
    private SimpleStringProperty userName;

    /**
     * Score in the table.
     */
    private SimpleStringProperty score;

    /**
     * Generates an empty {@code ScoreBoard} object.
     */
    public ScoreBoard() {
    }

    /**
     * Generates a new {@code ScoreBoard} object.
     *
     * @param rowNumber the current row number in the table
     * @param userName  the username to be shown in the table
     * @param score     the user's score
     */
    public ScoreBoard(SimpleStringProperty rowNumber, SimpleStringProperty userName,
                      SimpleStringProperty score) {
        this.rowNumber = rowNumber;
        this.userName = userName;
        this.score = score;
    }

    /**
     * Generates a new {@code ScoreBoard} object.
     *
     * @param rowNumber the current row number in the table
     * @param userName  the username to be shown in the table
     * @param score     the user's score
     */
    public ScoreBoard(int rowNumber, String userName,
                      int score) {
        this.rowNumber = new SimpleStringProperty(String.valueOf(rowNumber));
        this.userName = new SimpleStringProperty(userName);
        this.score = new SimpleStringProperty(String.valueOf(score));
    }

    /**
     * The current row number in the table.
     *
     * @return the row number
     */
    public String getRowNumber() {
        return rowNumber.get();
    }

    /**
     * The row number property.
     *
     * @return row number property
     */
    public SimpleStringProperty rowNumberProperty() {
        return rowNumber;
    }

    /**
     * Sets the current row number in the table.
     *
     * @param rowNumber the current row number
     */
    public void setRowNumber(String rowNumber) {
        this.rowNumber.set(rowNumber);
    }

    /**
     * Gets the current username that it is in the table.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName.get();
    }

    /**
     * Gets the username property.
     *
     * @return property of the username
     */
    public SimpleStringProperty userNameProperty() {
        return userName;
    }

    /**
     * Sets the value of the username.
     *
     * @param userName the value to be set
     */
    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    /**
     * Gets the actual score.
     *
     * @return the score value
     */
    public String getScore() {
        return score.get();
    }

    /**
     * Gets the score property.
     *
     * @return property of score
     */
    public SimpleStringProperty scoreProperty() {
        return score;
    }

    /**
     * Sets the value of the score.
     *
     * @param score the value to be set
     */
    public void setScore(String score) {
        this.score.set(score);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScoreBoard that = (ScoreBoard) o;
        return Objects.equals(rowNumber, that.rowNumber) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowNumber, userName, score);
    }

    @Override
    public String toString() {
        return "ScoreBoard{" +
                "rowNumber=" + rowNumber +
                ", userName=" + userName +
                ", score=" + score +
                '}';
    }
}
