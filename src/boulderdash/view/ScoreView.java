/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.view;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

/**
 *
 * @author Jarno
 */
public class ScoreView {
    private HBox view;

    public ScoreView() {
        view = new HBox();
        view.setPadding(new Insets(0, 0, 0, 0));
    }    

    public HBox getView() {
        return view;
    }    
}
