/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.controller;

import boulderdash.model.MainModel;
import boulderdash.view.MainView;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 *
 * @author Jarno
 */
public class MainController {
    
    private final int LEVEL_TIME = 60; // seconds
    
    private static MainController controller;
    private MainView view;
    private MainModel model;
    private Stage mainStage;
    private ObservableMap<KeyCode, Boolean> pressedKeys;
    
    private MainController(MainView view, Stage mainStage, MainModel model) {
        this.view = view;
        this.model = model;
        this.mainStage = mainStage;
        Scene mainScene = new Scene(view.getView());
        pressedKeys = FXCollections.observableMap(new HashMap<KeyCode, Boolean>());
        
        mainScene.setOnKeyPressed(e -> pressedKeys.put(e.getCode(), true));
        mainScene.setOnKeyReleased(e -> pressedKeys.put(e.getCode(), false));
        
        this.mainStage.setScene(mainScene);
        this.mainStage.setTitle("BOULDER DASH 2019 !!!");
        this.mainStage.setResizable(false);
        
    }
    public static void init(MainView view, Stage mainStage, MainModel model) {
        if (controller != null) throw new IllegalStateException("Already initiated");
        controller = new MainController(view, mainStage, model);
        
    }
    public static MainController getInstance() {
        if (controller == null) throw new IllegalStateException("Not initiated");
        return controller;
    }
    public void start() {
        model.setGameEnded(false);
        model.setScore(0);
        GameViewController.getInstance().start();
        model.startTimer(LEVEL_TIME);
        mainStage.show();
    }

    public ObservableMap<KeyCode, Boolean> pressedKeysProperty() {
        return pressedKeys;
    }
    public Map<KeyCode, Boolean> getPressedKeys() {
        return pressedKeys;
    }
    public void updateTime() {
        model.updateTime();
    }
    public void setGameEnded(boolean gameEnded) {
        model.setGameEnded(gameEnded);
    }

    public void addToScore(int pointsToAdd) {
        model.addToScore(pointsToAdd);
    }
    public SimpleIntegerProperty timeLeftProperty() {
        return model.timeLeftProperty();
    }
    
    public SimpleBooleanProperty gameEndedProperty() {
        return model.gameEndedProperty();
    }
    
    public SimpleIntegerProperty scoreProperty() {
        return model.scoreProperty();
    }
    
}
