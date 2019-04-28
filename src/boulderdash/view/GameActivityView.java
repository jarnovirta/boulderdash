/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.view;

import boulderdash.model.TileType;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


/**
 *
 * @author Jarno
 */
public class GameActivityView {
    private GridPane view;
    public static final int ROWS = 20;
    public static final int COLUMNS = 25;
    
    private Image brownBrickHorizontal;
    private Image rockfordLeft;
    private Image brownBrickVertical;
    private Image rockfordRight;
    private Image diamond;
    private Image rock;
    private Image dirt;
        
    public GameActivityView() {

        view = new GridPane();
        view.setPadding(new Insets(0, 0, 0, 0));
        view.setAlignment(Pos.CENTER);
        loadImages();        
        drawOuterWalls();
    }
    
    private void loadImages() {
        brownBrickHorizontal = new Image("file:brown_brick_horizontal.jpg");
        rockfordLeft = new Image("file:rockford_left.jpg");
        rockfordRight = new Image("file:rockford_right.jpg");
        brownBrickVertical = new Image("file:brown_brick_vertical.jpg");
        diamond = new Image("file:diamond.jpg");
        rock = new Image("file:rock.jpg");
        dirt = new Image("file:dirt.jpg");
    }

    private void drawOuterWalls() {
        for (int col = 0; col < COLUMNS; col++) {
            view.add(new ImageView(brownBrickHorizontal), col, 0);
            view.add(new ImageView(brownBrickHorizontal), col, ROWS - 1);
        }
        for (int row = 1; row < ROWS - 1; row++) {
            view.add(new ImageView(brownBrickVertical), 0, row);
            view.add(new ImageView(brownBrickVertical), COLUMNS - 1, row);            
        }        
    }
    
    public void setTile(int x, int y, TileType type) {        
        // x & y params are inner play area coordinates, excluding outter walls
        x++;
        y++;
        Image img;
        switch (type) {
            case DIRT:
                img = dirt;
                break;
            case HERO_LEFT:
                img = rockfordLeft;
                break;
            case HERO_RIGHT:
                img = rockfordRight;
                break;
            case WALL_HORIZONTAL:
                img = brownBrickHorizontal;
                break;
            case WALL_VERTICAL:
                img = brownBrickVertical;
                break;
            case DIAMOND:
                img = diamond;
                break;
            case ROCK:
                img = rock;
                break;
            case TUNNEL:
                setTileToTunnel(x, y);
                return;            
            default:
                img = dirt;
        }
        view.add(new ImageView(img), x, y);
    }
    public void setTileToTunnel(int x, int y) {
        Rectangle rect = new Rectangle(40, 40);
        rect.setFill(Color.BLACK);
        GridPane.setRowIndex(rect, y);
        GridPane.setColumnIndex(rect, x);        
        view.getChildren().addAll(rect);
    }
    
    public void setTile(Point2D position, TileType type) {
        setTile((int) position.getX(), (int) position.getY(), type);
    }
    
    public GridPane getView() {
        return view;
    }
}
