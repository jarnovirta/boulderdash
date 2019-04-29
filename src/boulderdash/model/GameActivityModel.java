/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.model;
import boulderdash.controller.MainController;
import boulderdash.domain.Hero;
import boulderdash.service.ImageService;
import boulderdash.view.GameActivityView;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;


/**
 *
 * @author Jarno
 */
public class GameActivityModel {
    
    public static final int GAME_AREA_START_ROW = 1;
    public static final int GAME_AREA_END_ROW = GameActivityView.ROWS - 2;
    public static final int GAME_AREA_START_COLUMN = 1;
    public static final int GAME_AREA_END_COLUMN = GameActivityView.COLUMNS - 2;
    
    private GameActivityView view;
        
    private SimpleObjectProperty<TileType>[][] tiles;
    private Hero hero;
        
    public GameActivityModel(GameActivityView view) {
        this.view = view;
        tiles = new SimpleObjectProperty[GameActivityView.COLUMNS][GameActivityView.ROWS];
        initTilesArray();        
        hero = new Hero();        
        bindViewToHeroProperties();
        bindHeroToKeyPresses();                
    }
    
    public void initTilesArray() {
        for (int row = 0; row < GameActivityView.ROWS; row++) {
            for (int col = 0; col < GameActivityView.COLUMNS; col++) {
                SimpleObjectProperty<TileType> tileType = new SimpleObjectProperty<>();
                final int x = col;
                final int y = row;
                tileType.addListener((obs, oldValue, newValue) -> view.setCellValue(x, y, getTileImage(newValue)));
                tiles[col][row] = tileType;
            }
        }
    }
    private void bindHeroToKeyPresses() {
        MainController.getInstance().pressedKeysProperty()
                .addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        if (MainController.getInstance().gameEndedProperty().getValue()) return;
                        Map<KeyCode, Boolean> pressedKeys = MainController.getInstance().getPressedKeys();
                        if (pressedKeys.getOrDefault(KeyCode.LEFT, Boolean.FALSE)) setHeroFacing(Direction.LEFT);
                        else if (pressedKeys.getOrDefault(KeyCode.RIGHT, Boolean.FALSE)) setHeroFacing(Direction.RIGHT);
                        else if (pressedKeys.getOrDefault(KeyCode.UP, Boolean.FALSE) ||
                                pressedKeys.getOrDefault(KeyCode.DOWN, Boolean.FALSE)) {
                            Direction dir = Math.random() < 0.5 ? Direction.LEFT : Direction.RIGHT;
                            setHeroFacing(dir);
                        }
                        else setHeroFacing(Direction.FORWARD);
                    }                    
        });        
    }
    private void bindViewToHeroProperties() {
        hero.positionProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != null) setTile(oldValue, TileType.TUNNEL);
            setTile(newValue, TileType.HERO);
            view.setCellValue((int) newValue.getX(), (int) newValue.getY(), 
                    hero.getImage());
        });
        hero.imageProperty().addListener((obs, oldValue, newValue) 
                -> view.setCellValue((int) hero.getPosition().getX(), 
                        (int) hero.getPosition().getY(), 
                        newValue));     
    }
    
    public void setTile(int x, int y, TileType type) {
        tiles[x][y].setValue(type);
    } 
    public void setTile(Point2D pos, TileType type) {
        setTile((int) pos.getX(), (int) pos.getY(), type);
    }
    public TileType getTile(int x, int y) {
        if (x < 0 || x > GameActivityView.COLUMNS - 1 || y < 0 || y > GameActivityView.ROWS - 1) {
            return null;
        }
        return tiles[x][y].getValue();
    } 
    public TileType getTile(Point2D position) {
        return getTile((int) position.getX(), (int) position.getY());
    } 

    public Point2D getHeroPosition() {
        return hero.getPosition();
    }

    public void setHeroPosition(Point2D heroPosition) {
        hero.setPosition(heroPosition);
    }
    public Direction getHeroFacing() {
        return hero.getFacing();
    }
    public void setHeroFacing(Direction direction) {
        hero.setFacing(direction);
    }

    public Image getTileImage(TileType type) {
        switch (type) {
            case DIRT:
                return ImageService.DIRT;
            case BRICK_WALL_HORIZONTAL:
                return ImageService.BRICK_WALL_HORIZONTAL;
            case BRICK_WALL_VERTICAL:
                return ImageService.BRICK_WALL_VERTICAL;
            case DIAMOND:
                return ImageService.DIAMOND;
            case ROCK:
                return ImageService.ROCK;
            case TUNNEL:
                return ImageService.TUNNEL;
            
        }
        return null;
    } 
}
