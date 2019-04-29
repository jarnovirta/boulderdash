/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boulderdash.domain;

import boulderdash.model.Direction;
import boulderdash.service.ImageService;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

/**
 *
 * @author Jarno
 */
public class Hero {
    private SimpleObjectProperty<Point2D> positionProperty;
    private Direction facing;
    private AnimationTimer animationTimer;
    private SimpleObjectProperty<Image> imageProperty;
    private boolean blink = false;
    
    public Hero() {
        facing = Direction.FORWARD;
        positionProperty = new SimpleObjectProperty();
        animationTimer = createAnimationTimer();
        imageProperty = new SimpleObjectProperty(ImageService.HERO_FORWARD);
        animationTimer.start();
    }
    private void setImage() {
        Image img = img = ImageService.HERO_LEFT;
        switch (facing) {
            case RIGHT:
                img = ImageService.HERO_RIGHT;
                break;
            case FORWARD:
                if (blink) img = ImageService.HERO_BLINK;
                else img = ImageService.HERO_FORWARD;
                break;
            case UP:
            case DOWN:
                img = Math.random() < 0.5 ? ImageService.HERO_RIGHT : ImageService.HERO_LEFT;
        }
        imageProperty.setValue(img);
    }
    private AnimationTimer createAnimationTimer() {
        return new AnimationTimer() {
            long previous;            
            @Override
            public void handle(long now) {
                long interval = 1_500_000_000;
                if (imageProperty.getValue() == ImageService.HERO_BLINK) interval = 200_000_000;
                
                if (now - previous < interval) return;
                previous = now;
                blink = !blink;
                setImage();
            }
            public void start() {
                previous =  System.nanoTime();
                super.start();
            }
        };
    }
    public Direction getFacing() {
        return facing;
    }

    public void setFacing(Direction facing) {
        animationTimer.stop();
        this.facing = facing;
        setImage();
        if (facing == Direction.FORWARD) animationTimer.start();
    }
    public SimpleObjectProperty<Image> imageProperty() {
        return imageProperty;
    }
    public Image getImage() {
        return imageProperty.getValue();
    }
    
    public SimpleObjectProperty<Point2D> positionProperty() {
        return positionProperty;
    }

    public Point2D getPosition() {
        return positionProperty.getValue();
    }

    public void setPosition(Point2D position) {
        this.positionProperty.setValue(position);
    }
    public void stopAnimation() {
        animationTimer.stop();
    }
}
