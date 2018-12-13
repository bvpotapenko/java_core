package ru.bvpotapenko.se.lesson1.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.bvpotapenko.se.lesson1.model.obstacle.Obstacle;

import java.util.ArrayList;
import java.util.List;

public class TrackObstacles extends AbstractTrackObject{
    private List<Obstacle> obstacles;
    int distanceBetweenObstacles;
    private int trackIntervalsAmount;
    private int trackLength;

    public TrackObstacles(int x, int y, Color color, GraphicsContext gc, int[] obstacles, int trackLength) {
        super(x, y, color, gc);
        this.obstacles = new ArrayList<>(obstacles.length);
        trackIntervalsAmount = obstacles.length + 1;
        this.trackLength = trackLength;
        initObstacles(obstacles);
    }

    private void initObstacles(int[] arrObstacles) {
        distanceBetweenObstacles = (trackLength - x) / trackIntervalsAmount;
        for (int obstacleIndex = 1; obstacleIndex <= arrObstacles.length; obstacleIndex++) {
            int obstacleX = x + distanceBetweenObstacles * obstacleIndex;
            int obstacleY = y;
            Obstacle o = new Obstacle(obstacleX, obstacleY,
                    Color.BROWN, gc,
                    arrObstacles[obstacleIndex - 1],
                    Color.GREEN, Color.RED);
            this.obstacles.add(o);
        }
    }

    public List<Obstacle> getObstacles(){
        return obstacles;
    }

    @Override
    public void drawShape() {
        obstacles.forEach(Obstacle::drawShape);
    }
}
