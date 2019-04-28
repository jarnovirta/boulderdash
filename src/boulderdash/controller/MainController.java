/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.controller;

import boulderdash.view.MainView;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Jarno
 */
public class MainController {
    private static MainController controller;
    private MainView view;
    private Scene mainScene;
    private Stage mainStage;
    
    private MainController(MainView view, Stage mainStage) {
        this.view = view;
        this.mainStage = mainStage;
        mainScene = new Scene(view.getView());
        this.mainStage.setScene(mainScene);
        this.mainStage.setTitle("BOULDER DASH 2019 !!!");
        this.mainStage.setResizable(false);
    }
    public static void init(MainView view, Stage mainStage) {
        if (controller != null) throw new IllegalStateException();
        controller = new MainController(view, mainStage);
    }
    public static MainController getInstance() {
        if (controller == null) throw new IllegalStateException();
        return controller;
    }
    public void start() {
        GameViewController.getInstance().startAnimationTimer();
        mainStage.show();
    }
}
