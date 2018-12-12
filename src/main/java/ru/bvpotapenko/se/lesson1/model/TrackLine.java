package ru.bvpotapenko.se.lesson1.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TrackLine extends AbstractTrackObject {
   // private int trackOffset;
    private int trackLength;

    public TrackLine(int x, int y, Color color, GraphicsContext gc, int trackLength) {
        super(x, y, color, gc);
        //this.trackOffset = trackOffset;
        this.trackLength = trackLength;
    }

    @Override
    public void drawShape() {
        gc.strokeLine(x, y , x + trackLength, y);
    }
}
