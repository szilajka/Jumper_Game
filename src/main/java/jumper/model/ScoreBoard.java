package jumper.model;

import javafx.beans.property.SimpleStringProperty;

public class ScoreBoard {
    private SimpleStringProperty rowNumber;
    private SimpleStringProperty userName;
    private SimpleStringProperty score;

    public ScoreBoard() {
    }

    public ScoreBoard(SimpleStringProperty rowNumber, SimpleStringProperty userName,
                      SimpleStringProperty score) {
        this.rowNumber = rowNumber;
        this.userName = userName;
        this.score = score;
    }

    public ScoreBoard(int rowNumber, String userName,
                      int score) {
        this.rowNumber = new SimpleStringProperty(String.valueOf(rowNumber));
        this.userName = new SimpleStringProperty(userName);
        this.score = new SimpleStringProperty(String.valueOf(score));
    }

    public String getRowNumber() {
        return rowNumber.get();
    }

    public SimpleStringProperty rowNumberProperty() {
        return rowNumber;
    }

    public void setRowNumber(String rowNumber) {
        this.rowNumber.set(rowNumber);
    }

    public String getUserName() {
        return userName.get();
    }

    public SimpleStringProperty userNameProperty() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public String getScore() {
        return score.get();
    }

    public SimpleStringProperty scoreProperty() {
        return score;
    }

    public void setScore(String score) {
        this.score.set(score);
    }
}
