/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import javafx.scene.text.Font;

/**
 *
 * @author Jarno
 */
public class ScoreView {
    private BorderPane view;
    private Label timeLeftLabel;
    private Label scoreLabel;
    private Label gameOverLabel;
    private Button resetButton;
    
    public ScoreView() {
        view = new BorderPane();
        view.setPadding(new Insets(5, 30, 5, 30));
                
        timeLeftLabel = getLabel();                
        scoreLabel = getLabel();        
        gameOverLabel = getLabel();
        
        resetButton = new Button("Reset");
        resetButton.setFont(Font.font(15));
        view.setLeft(timeLeftLabel);
        view.setCenter(scoreLabel);
        view.setRight(resetButton);        
    }    

    private Label getLabel() {
        Label label = new Label();    
        label.setFont(Font.font(25));
        return label;
    }
    public BorderPane getView() {
        return view;
    }    

    public Label getTimeLeftLabel() {
        return timeLeftLabel;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }
    
    public Button getResetButton() {
        return resetButton;
    }
    
}
