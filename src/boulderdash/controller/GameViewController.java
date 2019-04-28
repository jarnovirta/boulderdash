/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.controller;

import boulderdash.model.Direction;
import boulderdash.model.GameActivityModel;
import static boulderdash.model.GameActivityModel.GAME_AREA_COLUMNS;
import static boulderdash.model.GameActivityModel.GAME_AREA_ROWS;
import boulderdash.model.TileType;
import boulderdash.view.GameActivityView;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 *
 * @author Jarno
 */
public class GameViewController {
    private static GameViewController controller;
    private GameActivityView view;
    private GameActivityModel model;
    
    private GameViewController(GameActivityView view, GameActivityModel model) {
        this.view = view;
        this.model = model;
        createDirt();
        createWalls();
        model.setHeroPosition(new Point2D(GAME_AREA_COLUMNS / 2, GAME_AREA_ROWS / 2));
        model.setTile(model.getHeroPosition(), TileType.HERO_LEFT);
        model.setTile(10, 10, TileType.DIAMOND);        
    }
    
    public static void init(GameActivityView view, GameActivityModel model) {
        if (controller != null) throw new IllegalStateException();
        controller = new GameViewController(view, model);
    }
    public static GameViewController getInstance() {
        if (controller == null) throw new IllegalStateException();
        return controller;
    }
    public void startAnimationTimer() {
        new AnimationTimer() {
            long prevUpdate = System.nanoTime();
            
            @Override
            public void handle(long now) {
                if (now - prevUpdate < 500_000_000) {
                    return;
                }
                prevUpdate = now;
            }
        }.start();
    }

    private void createDirt() {
        for (int row = 0; row < GAME_AREA_ROWS; row++) {
            for (int col = 0; col < GAME_AREA_COLUMNS; col++) {
                model.setTile(col, row, TileType.DIRT);
            }
        }
    }
    private void createWalls() {
        int wallTileCount = 0;

        Random rand = new Random();
        while (wallTileCount < 50) {
            int startX = rand.nextInt(GAME_AREA_COLUMNS);
            int startY = rand.nextInt(GAME_AREA_ROWS);
            TileType tileType = rand.nextInt(2) == 0 ? TileType.WALL_HORIZONTAL : TileType.WALL_VERTICAL;
            int maxLength = tileType == TileType.WALL_HORIZONTAL ? 
                    GAME_AREA_COLUMNS - startX - 1 : GAME_AREA_ROWS - startY - 1;
            if (maxLength <= 0) {
                continue;
            }
            int length = 1 + rand.nextInt(maxLength);

            for (int i = 0; i < length; i++) {
                int x;
                int y;
                if (tileType == TileType.WALL_HORIZONTAL) {
                    x = startX + i;
                    y = startY;
                } else {
                    x = startX;
                    y = startY + i;
                }

                model.setTile(x, y, tileType);
                wallTileCount++;
            }
        }
    }
}
