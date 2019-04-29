/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.model;

import boulderdash.view.MainView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Jarno
 */
public class MainModel {
    private MainView view;
    private SimpleBooleanProperty gameEnded;
    private long startTime; // milliseconds
    private int levelTime;
    private SimpleIntegerProperty timeLeft;
    private SimpleIntegerProperty score;
    
    public MainModel(MainView view) {
        this.view = view;
        timeLeft = new SimpleIntegerProperty();
        gameEnded = new SimpleBooleanProperty(false);
        score = new SimpleIntegerProperty(0);
    }
   
    public void setGameEnded(boolean gameEnded) {
        this.gameEnded.setValue(gameEnded);
    }  
    public void startTimer(int levelTime) {
        this.levelTime = levelTime;
        timeLeft.setValue(levelTime);
        startTime = System.currentTimeMillis();
    }
    int test = 0;
    public void updateTime() {
        int elapsed = (int) (System.currentTimeMillis() - startTime) / 1000;
        int timeLeftNow = levelTime - elapsed;
        timeLeft.setValue(timeLeftNow);
        if (timeLeftNow == 0) setGameEnded(true);
    }
    public SimpleIntegerProperty timeLeftProperty() {
        return timeLeft;
    }
    public SimpleBooleanProperty gameEndedProperty() {
        return gameEnded;
    }
    public SimpleIntegerProperty scoreProperty() {
        return score;
    }
    public void setScore(int score) {
        this.score.setValue(score);
    }
    public void addToScore(int pointsToAdd) {
        score.setValue(score.getValue().intValue() + pointsToAdd);
    }
}
