/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Jarno
 */
public class GameActivityView {
    private GridPane view;
    
    public static final int ROWS = 20;
    public static final int COLUMNS = 25;
    private ImageView[][] imageViewGrid;
        
    public GameActivityView() {
        view = new GridPane();
        view.setPadding(new Insets(0, 0, 0, 0));
        view.setAlignment(Pos.CENTER);
        initImageViewGrid();
    }
    private void initImageViewGrid() {
        imageViewGrid = new ImageView[COLUMNS][ROWS];
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                ImageView imageView = new ImageView();
                view.add(imageView, x, y);
                imageViewGrid[x][y] = imageView;
            }
        }
    }
    public GridPane getView() {
        return view;
    }

    public void setCellValue(int x, int y, Image image) {
        imageViewGrid[x][y].setImage(image);
    }    
}
