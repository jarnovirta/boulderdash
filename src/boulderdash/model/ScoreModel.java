/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.model;

import boulderdash.controller.MainController;
import boulderdash.view.ScoreView;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Jarno
 */
public class ScoreModel {
    private ScoreView view;
    
    public ScoreModel(ScoreView view) {
        this.view = view;
        MainController mainController = MainController.getInstance();
        mainController
                .timeLeftProperty()
                .addListener((obs, oldValue, newValue) -> {
                    view.getTimeLeftLabel().setText("Time: " + newValue.intValue());
                });
        mainController
                .gameEndedProperty()
                .addListener((obs, oldValue, newValue) -> {
                    if (newValue.booleanValue() == true) view.getTimeLeftLabel().setText("GAME OVER!");
                });
        view.getScoreLabel().setText("Score: 0");
        mainController
                .scoreProperty()
                .addListener((obs, oldValue, newValue) -> {
                    view.getScoreLabel().setText("Score: " + newValue.intValue());
                });
    }
}
