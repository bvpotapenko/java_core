package ru.bvpotapenko.se.lesson1.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.bvpotapenko.se.lesson1.console.TeamMember;

public class TeamMemberToken extends AbstractTrackObject{
    private final int RADIUS = 9;
    private TeamMember teamMember;

    public TeamMemberToken(int x, int y, Color color, GraphicsContext gc, TeamMember teamMember) {
        super(x, y, color, gc);
        this.teamMember = teamMember;
    }

    @Override
    public void drawShape() {
        gc.setFill(color);
        gc.fillOval(x+1, y-RADIUS * 2-1, RADIUS * 2, RADIUS * 2);
         if(color.equals(Color.WHITE)) {
            gc.setStroke(Color.WHITE);
             System.out.println("Token is WHITE");
        }else{
             gc.setStroke(Color.BLACK);
         }
        gc.strokeText("" + teamMember.getNumber(), x+7, y-5);
    }

}
