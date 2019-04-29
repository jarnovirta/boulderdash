/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.controller;

import boulderdash.model.Direction;
import boulderdash.model.GameActivityModel;
import boulderdash.model.TileType;
import boulderdash.view.GameActivityView;

import static boulderdash.model.GameActivityModel.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
        createOuterWalls();
        createDirt();
                
        createWalls();
        createObjects(TileType.DIAMOND, DIAMONDS);
        createObjects(TileType.ROCK, ROCKS);
        
        int heroX = (GAME_AREA_START_COLUMN + GAME_AREA_END_COLUMN) / 2;
        int heroY = (GAME_AREA_START_ROW + GAME_AREA_END_ROW) / 2;
        model.setHeroPosition(new Point2D(heroX, heroY));
        
        animationTimer.start();
    }
    private boolean moveHero(Direction direction) {
        Point2D currentPos = model.getHeroPosition();
        
        Point2D destination = currentPos.add(getMovementPoint(direction));
        TileType destinationTile = model.getTile(destination);
        if (destinationTile == null || destinationTile == TileType.BRICK_WALL_HORIZONTAL
                || destinationTile == TileType.BRICK_WALL_VERTICAL
                || destinationTile == TileType.ROCK) {
            return false;
        }
        model.setTile(currentPos, TileType.TUNNEL);
        model.setHeroPosition(destination);
                
        if (destinationTile == TileType.DIAMOND) MainController.getInstance().addToScore(100);
        checkForNewFallingObjects((int) currentPos.getX(), (int) currentPos.getY());

        return true;
    }
    private void checkForNewFallingObjects(int x, int y) {
        Set<Point2D> fallingObjects = model.getFallingObjectPositions();
        for (int row = y - 1; row <= y + 1; row++) {
            if (row > GAME_AREA_END_ROW) continue;
            for (int col = x - 1; col <= x + 1 ; col++) {
                if (col > GAME_AREA_END_COLUMN) continue;                
                Point2D point = new Point2D(col, row);                
                if (getPossibleObjectFallPoints(col, row).size() > 0
                        && !fallingObjects.contains(point)) {
                    fallingObjects.add(point);
                }
            }
        }
    }

    private Set<Point2D> getPossibleObjectFallPoints(int objectX, int objectY) {
        Set<Point2D> fallPointsList = new HashSet<>();
        TileType tile = model.getTile(objectX, objectY);
        
        if (objectY == GAME_AREA_END_ROW) return fallPointsList;
        if (tile != TileType.DIAMOND && tile != TileType.ROCK) return fallPointsList;        
        
        TileType[][] tiles = model.getTileTypesAroundPoint(objectX, objectY);        
        
        TileType tileUnder = tiles[1][1];
        Point2D pointUnder = new Point2D(objectX, objectY + 1);
        if (tileUnder == TileType.TUNNEL) {            
            fallPointsList.add(pointUnder);
        }
        else if (tileUnder == TileType.HERO 
                && model.getLastAnimationFallingRocks().contains(new Point2D(objectX, objectY))) {
            fallPointsList.add(pointUnder);
        }
        // Handle falling off to the side off a rock, edge of wall, or a diamond
        else if (tileUnder != TileType.HERO && tileUnder != TileType.DIRT) {
            if (tiles[0][0] == TileType.TUNNEL && tiles[1][0] == TileType.TUNNEL) {
                fallPointsList.add(new Point2D(objectX - 1, objectY));
            }
            if (tiles[0][2] == TileType.TUNNEL && tiles[1][2] == TileType.TUNNEL) {
                fallPointsList.add(new Point2D(objectX + 1, objectY));
            }
        }        
        return fallPointsList;
    }
    
    private void moveFallingObjects() {
        List<Point2D> movedFromPositions = new ArrayList<>();
        Set<Point2D> fallingObjects = model.getFallingObjectPositions();
        
        Set<Point2D> lastAnimationFallingRocks = model.getLastAnimationFallingRocks();
        Set<Point2D> fallenRocksNewPositions = new HashSet<>();
        
        Iterator<Point2D> iter = fallingObjects.iterator();
        
        // Iterate over objects which can fall, then remove them from 
        // the falling objects Set. After moving objects all 
        // squares around the moved object are checked for objects which
        // can now fall
        while (iter.hasNext()) {
            Point2D fallingObjectPosition = iter.next();
            Set<Point2D> fallingPoints = getPossibleObjectFallPoints(
                    (int) fallingObjectPosition.getX(), 
                    (int) fallingObjectPosition.getY());
            if (fallingPoints.size() == 0) continue;
            List<Point2D> pointsList = new ArrayList<>(fallingPoints);
            Collections.shuffle(pointsList);
            Point2D destination = pointsList.get(0);
            
            TileType fallingObject = model.getTile(fallingObjectPosition);
            TileType tileAtDestination = 
                    model.getTile(destination);
            
            if (fallingObject == TileType.ROCK){
                fallenRocksNewPositions.add(destination);
                if (tileAtDestination == TileType.HERO 
                        && lastAnimationFallingRocks.contains(fallingObjectPosition)) {
                    MainController.getInstance().setGameEnded(true);
                }
            }
            
            model.setTile(destination, fallingObject);
            model.setTile(fallingObjectPosition, TileType.TUNNEL);
            
            movedFromPositions.add(fallingObjectPosition);
            iter.remove();
                       
        }
        model.setLastAnimationFallingRocks(fallenRocksNewPositions);
        
        // Check for new falling objects
        for (Point2D oldPosition : movedFromPositions) {
            checkForNewFallingObjects((int) oldPosition.getX(), (int) oldPosition.getY());
        }
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
    
    private boolean handleHeroMovementCommands() {
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
            long fallingObjectsMovedTime = 0;
            @Override
            public void handle(long now) {
                if (now - prevUpdateTime < UPDATE_INTERVAL) {
                    return;
                }
                MainController.getInstance().updateTime();
                prevUpdateTime = now;
                if (now - prevMovementTime > MOVEMENT_INTERVAL) {
                    if (handleHeroMovementCommands()) prevMovementTime = now;
                }
                if (now - fallingObjectsMovedTime > MOVEMENT_INTERVAL) {
                    fallingObjectsMovedTime = now;
                    moveFallingObjects();
                }
            }
        };
    }
    private void createOuterWalls() {
        for (int col = 0; col < GameActivityView.COLUMNS; col++) {
            model.setTile(col, 0, TileType.BRICK_WALL_HORIZONTAL);
            model.setTile(col, GameActivityView.ROWS - 1, TileType.BRICK_WALL_HORIZONTAL);            
        }
        for (int row = 0; row < GameActivityView.ROWS; row++) {
            model.setTile(0, row, TileType.BRICK_WALL_VERTICAL);
            model.setTile(GameActivityView.COLUMNS - 1, row, TileType.BRICK_WALL_VERTICAL);                       
        }        
    } 
    private void createDirt() {
        for (int row = GameActivityModel.GAME_AREA_START_ROW; row <= GameActivityModel.GAME_AREA_END_ROW; row++) {
            for (int col = GameActivityModel.GAME_AREA_START_COLUMN; col <= GameActivityModel.GAME_AREA_END_COLUMN; col++) {
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
            int x = GAME_AREA_START_COLUMN + rand.nextInt(GAME_AREA_END_COLUMN + 1);
            int y = GAME_AREA_START_ROW + rand.nextInt(GAME_AREA_END_ROW + 1);
            if (model.getTile(x, y) == TileType.DIRT) return new Point2D(x, y);
        }
    }
    
    private Point2D getWallStartPoint(TileType wallType) {
        Random rand = new Random();
        int x = GAME_AREA_START_COLUMN + 1 + rand.nextInt(GAME_AREA_END_COLUMN - 2);
        int y = GAME_AREA_START_ROW + 1 + rand.nextInt(GAME_AREA_END_ROW - 2);

        if (wallType == TileType.BRICK_WALL_HORIZONTAL) {
            if (y == GAME_AREA_START_ROW) y++;
            if (y == GAME_AREA_END_ROW) y--;
        }
        else {
            if (x == GAME_AREA_START_COLUMN) x++;
            if (x == GAME_AREA_END_COLUMN - 1) x--;
        }
        return new Point2D(x, y);
    } 
    private void createWalls() {
        int wallTileCount = 0;
        Random rand = new Random();
        while (wallTileCount < WALL_TILES) {
            TileType tileType = rand.nextInt(2) == 0 ? TileType.BRICK_WALL_HORIZONTAL : 
                    TileType.BRICK_WALL_VERTICAL;
            Point2D startPoint = getWallStartPoint(tileType);
            
            int startX = (int) startPoint.getX();
            int startY = (int) startPoint.getY();
            
            int maxWidth = GAME_AREA_END_COLUMN - startX + 1;
            int maxHeight = GAME_AREA_END_ROW - startY + 1;
            
            int maxLength = tileType == TileType.BRICK_WALL_HORIZONTAL ? 
                    maxWidth : maxHeight;
            if (maxLength <= 0) {
                continue;
            }
            int length = 1 + rand.nextInt(maxLength);
            for (int i = 0; i < length; i++) {
                int x;
                int y;
                if (tileType == TileType.BRICK_WALL_HORIZONTAL) {
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
