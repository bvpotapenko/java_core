package ru.bvpotapenko.se.lesson1.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class AbstractTrackObject {
    int x;
    int y;
    Color color;
    GraphicsContext gc;

    public AbstractTrackObject(int x, int y, Color color, GraphicsContext gc) {
        this.x = x;
        this.y = y;
        this.gc = gc;
        this.color = color;

    }

    public GraphicsContext getGraphicsContext(){
        return gc;
    }

    public Color getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public abstract void drawShape();
}
