package ru.bvpotapenko.se.lesson1.model.obstacle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.bvpotapenko.se.lesson1.model.AbstractTrackObject;

public class Obstacle extends AbstractTrackObject {
    private final int OBSTACLE_WIDTH = 20;
    private int obstacleHeight;
    private int OBSTACLE_HEIGHT_MULTIPLIER = 5;
    Color passedColor;
    Color failedColor;
    private ObstacleState state;

    public Obstacle(int x, int y, Color readyColor, GraphicsContext gc, int obstacleHeight, Color passedColor, Color failedColor) {
        super(x, y, readyColor, gc);
        this.obstacleHeight = obstacleHeight;
        state = ObstacleState.READY;
        this.passedColor = passedColor;
        this.failedColor = failedColor;
    }
    public void setState(ObstacleState state){
        this.state = state;
        drawShape();
    }

    public ObstacleState getState() {
        return state;
    }

    @Override
    public void drawShape() {
        switch(state) {
            case READY:
                getGraphicsContext().setFill(getColor());
                break;
            case PASSED:
                getGraphicsContext().setFill(passedColor);
                break;
            case FAILED:
                getGraphicsContext().setFill(failedColor);
                break;
        }
        getGraphicsContext().fillRect(getX(), getY() - obstacleHeight * OBSTACLE_HEIGHT_MULTIPLIER,
                OBSTACLE_WIDTH, obstacleHeight * OBSTACLE_HEIGHT_MULTIPLIER);
    }

    public int getObstacleHeight() {
        return obstacleHeight;
    }

    public int getOBSTACLE_WIDTH() {
        return OBSTACLE_WIDTH;
    }

    public int getOBSTACLE_HEIGHT_MULTIPLIER() {
        return OBSTACLE_HEIGHT_MULTIPLIER;
    }
}
