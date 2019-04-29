/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash;

import boulderdash.controller.GameViewController;
import boulderdash.controller.MainController;
import boulderdash.controller.ScoreViewController;
import boulderdash.model.GameActivityModel;
import boulderdash.model.MainModel;
import boulderdash.model.ScoreModel;
import boulderdash.service.ImageService;
import boulderdash.view.MainView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author Jarno
 */
public class App extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        MainView mainView = new MainView();
        MainController.init(mainView, primaryStage, new MainModel(mainView));
        
        GameActivityModel gameActivityModel = new GameActivityModel(mainView.getGameActivityView());
        GameViewController.init(mainView.getGameActivityView(), gameActivityModel);
        
        ScoreModel scoreModel = new ScoreModel(mainView.getScoreView());
        ScoreViewController.init(mainView.getScoreView(), scoreModel);
        
        MainController.getInstance().start();
    }    
}
