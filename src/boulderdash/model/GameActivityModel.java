/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.model;
import boulderdash.view.GameActivityView;
import java.util.Random;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;


/**
 *
 * @author Jarno
 */
public class GameActivityModel {
    private GameActivityView view;
    public static final int GAME_AREA_ROWS = GameActivityView.ROWS - 2;
    public static final int GAME_AREA_COLUMNS = GameActivityView.COLUMNS - 2;
    
    private SimpleObjectProperty<TileType>[][] tiles;
    private Point2D heroPosition;
        
    public GameActivityModel(GameActivityView view) {
        this.view = view;
        tiles = new SimpleObjectProperty[GAME_AREA_COLUMNS][GAME_AREA_ROWS];
        initiateTilesArray();
    }
    public void setTile(int x, int y, TileType type) {
        tiles[x][y].setValue(type);
    }
    public void setTile(Point2D pos, TileType type) {
        GameActivityModel.this.setTile((int) pos.getX(), (int) pos.getY(), type);
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
}
