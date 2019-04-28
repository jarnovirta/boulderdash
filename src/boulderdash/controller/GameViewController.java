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
import java.util.Map;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;


/**
 *
 * @author Jarno
 */
public class GameViewController {
    private static GameViewController controller;
    private GameActivityView view;
    private GameActivityModel model;
    private AnimationTimer animationTimer;
    
    // Map content constants
    private final int WALL_TILES = 50;
    private final int DIAMONDS = 30;
    private final int ROCKS = 50;
                
    // Timings for AnimationTimer
    private final int FPS = 60;
    private final long UPDATE_INTERVAL = 1_000_000_000 / FPS;
    private final int MOVEMENTS_PER_SECOND = 7;
    private final long MOVEMENT_INTERVAL = 1_000_000_000 / MOVEMENTS_PER_SECOND;
    
    private GameViewController(GameActivityView view, GameActivityModel model) {
        this.view = view;
        this.model = model;
        this.animationTimer = getAnimationTimer();
 
        MainController.getInstance().gameEndedProperty()
                .addListener((obs, oldValue, newValue) -> {
                    if (newValue.booleanValue() == true) animationTimer.stop();
                });
    }   

    public static void init(GameActivityView view, GameActivityModel model) {
        if (controller != null) throw new IllegalStateException("Already initiated");
        controller = new GameViewController(view, model);
    }
    public static GameViewController getInstance() {
        if (controller == null) throw new IllegalStateException("Not initiated");
        return controller;
    }
    
    public void start() {
        animationTimer.stop();
        createDirt();
        createWalls();
        model.setHeroPosition(new Point2D(GAME_AREA_COLUMNS / 2, GAME_AREA_ROWS / 2));
        model.setTile(model.getHeroPosition(), TileType.HERO_LEFT);
        createObjects(TileType.DIAMOND, DIAMONDS);
        createObjects(TileType.ROCK, ROCKS);
        animationTimer.start();
    }
    private boolean moveHero(Direction direction) {
        Point2D currentPos = model.getHeroPosition();
        Point2D destination = currentPos.add(getMovementPoint(direction));
        TileType destinationTile = model.getTile(destination);
        if (destinationTile == null || destinationTile == TileType.WALL_HORIZONTAL
                || destinationTile == TileType.WALL_VERTICAL
                || destinationTile == TileType.ROCK) {
            return false;
        }
        model.setTile(currentPos, TileType.TUNNEL);
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            model.setHeroFacingDirection(direction);
        }
        TileType heroTileType = model.getHeroFacingDirection() == Direction.LEFT ? 
                TileType.HERO_LEFT : TileType.HERO_RIGHT;
        model.setTile(destination, heroTileType);
        model.setHeroPosition(destination);
        
        if (destinationTile == TileType.DIAMOND) MainController.getInstance().addToScore(100);
        return true;
    }
    private Point2D getMovementPoint(Direction direction) {
        int x = direction == Direction.LEFT ? - 1 : 
                direction == Direction.RIGHT ? + 1 :
                0;
        int y = direction == Direction.UP ? - 1 :
                direction == Direction.DOWN ? + 1 :
                0;
        return new Point2D(x, y);
    }
    
    // handleMovementKeysPressed only moves Hero in one direction at a time.
    // Next movement only after MOVEMENT_INTERVAL. Order of handling 
    // is reversed every second time to ensure movement up/down + left/right
    // when two keys are pressed at once.
    private static boolean reverseKeyOrder = false;
    
    private boolean handleMovementKeysPressed() {
        reverseKeyOrder = !reverseKeyOrder;
        Map<KeyCode, Boolean> pressedKeys = MainController.getInstance().getPressedKeys();
        KeyCode[] movementCodes = { KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.DOWN };
        for (int i = 0; i < movementCodes.length; i++) {
            int index = i;
            if (reverseKeyOrder) index = movementCodes.length - 1 - i;
            KeyCode code = movementCodes[index];
            if (pressedKeys.getOrDefault(code, false)) {
                if (moveHero(Direction.valueOf(code.toString()))) return true;                
            }
        }
        return false;
    }
    private  AnimationTimer getAnimationTimer() {
        return new AnimationTimer() {
            long prevUpdateTime = System.nanoTime();
            long prevMovementTime = 0;

            @Override
            public void handle(long now) {
                if (now - prevUpdateTime < UPDATE_INTERVAL) {
                    return;
                }
                MainController.getInstance().updateTime();
                prevUpdateTime = now;
                if (now - prevMovementTime > MOVEMENT_INTERVAL) {
                    if (handleMovementKeysPressed()) prevMovementTime = now;                    
                }
            }
        };
    }

    private void createDirt() {
        for (int row = 0; row < GAME_AREA_ROWS; row++) {
            for (int col = 0; col < GAME_AREA_COLUMNS; col++) {
                model.setTile(col, row, TileType.DIRT);
            }
        }
    }
    private void createObjects(TileType type, int targetCount) {
        int count = 0;
        while (count++ < targetCount) {
            model.setTile(getEmptyTile(), type);            
        }
    }
    
    private Point2D getEmptyTile() {
        Random rand = new Random();
        while (true) {
            int x = rand.nextInt(GAME_AREA_COLUMNS);
            int y = rand.nextInt(GAME_AREA_ROWS);
            if (model.getTile(x, y) == TileType.DIRT) return new Point2D(x, y);
        }
    }
    private Point2D getWallStartPoint(TileType wallType) {
        Random rand = new Random();
        int x = rand.nextInt(GAME_AREA_COLUMNS);
        int y = rand.nextInt(GAME_AREA_ROWS);

        if (wallType == TileType.WALL_HORIZONTAL) {
            if (y == 0) y++;
            if (y == GAME_AREA_ROWS - 1) y--;
        }
        else {
            if (x == 0) x++;
            if (x == GAME_AREA_COLUMNS - 1) x--;
        }

        return new Point2D(x, y);
    }
    private void createWalls() {
        int wallTileCount = 0;
        Random rand = new Random();
        while (wallTileCount < WALL_TILES) {
            TileType tileType = rand.nextInt(2) == 0 ? TileType.WALL_HORIZONTAL : TileType.WALL_VERTICAL;
            Point2D startPoint = getWallStartPoint(tileType);
            int startX = (int) startPoint.getX();
            int startY = (int) startPoint.getY();
            
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
