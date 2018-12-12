package ru.bvpotapenko.se.lesson1.model.flag;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.bvpotapenko.se.lesson1.model.AbstractTrackObject;

public class TrackFlag extends AbstractTrackObject {
    private final static int FLAG_POLE_HEIGHT = 30;
    private final int FLAG_HEIGHT = 10;
    private final int FLAG_WIDTH = 20;
    private FlagDirection flagDirection;

    public TrackFlag(int x, int y, Color color, GraphicsContext gc, FlagDirection flagDirection) {
        super(x, y-FLAG_POLE_HEIGHT, color, gc);
        this.flagDirection = flagDirection;
    }

    @Override
    public void drawShape() {
        getGraphicsContext().setFill(getColor());
        getGraphicsContext().strokeLine(getX(), getY(), getX(), getY() + FLAG_POLE_HEIGHT);

        if (flagDirection == FlagDirection.LEFT)
            getGraphicsContext().fillRect(getX()-FLAG_WIDTH, getY(), FLAG_WIDTH, FLAG_HEIGHT);
        else
            getGraphicsContext().fillRect(getX(), getY(), FLAG_WIDTH, FLAG_HEIGHT);
    }
}
