/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.model;
import boulderdash.controller.MainController;
import boulderdash.view.GameActivityView;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;


/**
 *
 * @author Jarno
 */
public class GameActivityModel {
    private GameActivityView view;
    public static final int GAME_AREA_ROWS = GameActivityView.ROWS - 2;
    public static final int GAME_AREA_COLUMNS = GameActivityView.COLUMNS - 2;
    
    private SimpleObjectProperty<TileType>[][] tiles;
    private TileType heroTile;
    private Point2D heroPosition;
    private AnimationTimer blinkTimer;
        
    public GameActivityModel(GameActivityView view) {
        this.view = view;
        tiles = new SimpleObjectProperty[GAME_AREA_COLUMNS][GAME_AREA_ROWS];
        blinkTimer = getBlinkTimer();
        blinkTimer.start();
        heroTile = TileType.HERO_FORWARD;
        MainController.getInstance()
                .pressedKeysProperty()
                .addListener(new MapChangeListener<KeyCode, Boolean>() {
                    @Override
                    public void onChanged(MapChangeListener.Change<? extends KeyCode, ? extends Boolean> change) {
                        setHeroTile((Map<KeyCode, Boolean>) change.getMap());
                    }
                });
                
        initiateTilesArray();
    }
    private void setHeroTile(Map<KeyCode, Boolean> pressedKeys) {
        blinkTimer.stop();
        if (pressedKeys.getOrDefault(KeyCode.LEFT, Boolean.FALSE)) {
            heroTile = TileType.HERO_LEFT;
        }
        else if (pressedKeys.getOrDefault(KeyCode.RIGHT, Boolean.FALSE)) {
            heroTile = TileType.HERO_RIGHT;
        }
        else if (pressedKeys.getOrDefault(KeyCode.UP, Boolean.FALSE)
                || pressedKeys.getOrDefault(KeyCode.DOWN, Boolean.FALSE)) {
            heroTile = Math.random() < 0.5 ? TileType.HERO_LEFT : TileType.HERO_RIGHT;
        }
        else {
            heroTile = TileType.HERO_FORWARD;
            blinkTimer.start();
        }
        setTile(heroPosition, heroTile);
    }
    public void setTile(int x, int y, TileType type) {
        tiles[x][y].setValue(type);
    }
    public void setTile(Point2D pos, TileType type) {
        GameActivityModel.this.setTile((int) pos.getX(), (int) pos.getY(), type);
    }
    public TileType getTile(int x, int y) {
        if (x < 0 || x > GAME_AREA_COLUMNS - 1 || y < 0 || y > GAME_AREA_ROWS - 1) {
            return null;
        }
        return tiles[x][y].getValue();
    }
    public TileType getTile(Point2D position) {
        return getTile((int) position.getX(), (int) position.getY());
    }
    private AnimationTimer getBlinkTimer() {
        return new AnimationTimer() {
            long previous;
            
            @Override
            public void handle(long now) {
                long interval = 1_500_000_000;
                if (heroTile == TileType.HERO_BLINK) interval = 200_000_000;
                
                if (now - previous < interval) return;
                previous = now;
                if (heroTile == TileType.HERO_FORWARD) heroTile = TileType.HERO_BLINK;
                else heroTile = TileType.HERO_FORWARD;
                setTile(heroPosition, heroTile);
            }
            public void start() {
                previous =  System.nanoTime();
                super.start();
            }
        };
    }
    public void initiateTilesArray() {
        for (int row = 0; row < GAME_AREA_ROWS; row++) {
            for (int col = 0; col < GAME_AREA_COLUMNS; col++) {
                SimpleObjectProperty<TileType> tile = new SimpleObjectProperty<>();
                final int x = col;
                final int y = row;
                tile.addListener((obs, oldValue, newValue) -> view.setTile(x, y, newValue));
                tiles[col][row] = tile;
            }
        }
    }

    public Point2D getHeroPosition() {
        return heroPosition;
    }

    public void setHeroPosition(Point2D heroPosition) {
        this.heroPosition = heroPosition;
    }

    public TileType getHeroTile() {
        return heroTile;
    }    
}
