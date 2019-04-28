/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.controller;

import boulderdash.model.ScoreModel;
import boulderdash.view.ScoreView;

/**
 *
 * @author Jarno
 */
public class ScoreViewController {
    private static ScoreViewController controller;
    private ScoreView view;
    private ScoreModel model;

    private ScoreViewController(ScoreView view, ScoreModel model) {
        this.view = view;
        this.model = model;
        view.getResetButton().setOnAction(e -> MainController.getInstance().start());
    }
    public static void init(ScoreView view, ScoreModel model) {
        if (controller != null) throw new IllegalStateException("Already initiated");
        controller = new ScoreViewController(view, model);
    }
    public static ScoreViewController getInstance() {
        if (controller == null) throw new IllegalStateException("Not initiated");
        return controller;
    }
    
}
