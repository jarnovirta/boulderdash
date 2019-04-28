/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.view;

import java.awt.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

/**
 *
 * @author Jarno
 */
public class MainView {
    private VBox view;
    private ScoreView scoreView;
    private GameActivityView gameActivityView;
    
    public MainView() {
        view = new VBox();
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(0, 0, 0, 0));
        scoreView = new ScoreView();
        view.getChildren().add(scoreView.getView());
        gameActivityView = new GameActivityView();
        view.getChildren().add(gameActivityView.getView());
    }

    public VBox getView() {
        return view;
    }

    public ScoreView getScoreView() {
        return scoreView;
    }

    public GameActivityView getGameActivityView() {
        return gameActivityView;
    }
    
}
