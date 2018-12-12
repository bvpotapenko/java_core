package ru.bvpotapenko.se.lesson1.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.bvpotapenko.se.lesson1.console.Course;
import ru.bvpotapenko.se.lesson1.model.flag.FlagDirection;
import ru.bvpotapenko.se.lesson1.model.flag.TrackFlag;


public class Track {
    private int trackLength; // = 340;
    private int x;
    private int y;
    private GraphicsContext gc;
    private Course course;

    //    private int spaceBetweenTracks; // = 60;
//    private int trackPosition; // ->> offset trackPosition * spaceBetweenTracks
    TrackLine trackLine;
    TrackFlag trackStartFlag;
    TrackFlag trackFinishFlag;
    TrackObstacles trackObstacles;

    public Track(int trackLength, int x, int y, GraphicsContext gc, Course course) {
        this.trackLength = trackLength;
        this.x = x;
        this.y = y;
        this.gc = gc;
        this.course = course;
        initTrackObjects();
        drawTrack();
    }

    private void initTrackObjects() {
        trackLine = new TrackLine(x, y, Color.BLACK, gc, trackLength);
        trackStartFlag = new TrackFlag(x, y, Color.GREEN, gc, FlagDirection.RIGHT);
        trackFinishFlag = new TrackFlag(x + trackLength, y, Color.RED, gc, FlagDirection.LEFT);
        trackObstacles = new TrackObstacles(x, y, Color.BROWN, gc, course.getObstacles(), trackLength);
    }

    public void drawTrack(){
        trackLine.drawShape();
        trackStartFlag.drawShape();
        trackFinishFlag.drawShape();
        trackObstacles.drawShape();
    }
}
