package ru.bvpotapenko.se.lesson1.model;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.bvpotapenko.se.lesson1.console.TeamMember;

public class TeamMemberToken {
    private final int RADIUS = 9;
    StackPane stack;
    int x;
    int y;
    Color color;
    private TeamMember teamMember;
    private Track track;
    private Group group;

    public TeamMemberToken(int x, int y, Color color, TeamMember teamMember, Track track, Group group) {
        this.x = x;
        this.y = y;
        this.teamMember = teamMember;
        this.group = group;
        this.track = track;
    }

    public void drawShape() {
        Circle circle = new Circle(x + 11, y - 1 - RADIUS, RADIUS);
        circle.setFill(Color.YELLOW);
        Text text = new Text(x + 7, y - 5, "" + teamMember.getNumber());
        text.setFill(Color.BLACK);
        text.setFont(new Font(16));
        stack = new StackPane();

//       group.getChildren().addAll(circle, text);
        stack.getChildren().addAll(circle, text);
        stack.relocate(x + 1, y - RADIUS * 2 - 3);
        group.getChildren().add(stack);

    }

    public StackPane getStack() {
        return stack;
    }

    public Track getTrack() {
        return track;
    }

    public void removeToken(){
        group.getChildren().remove(stack);
    }
}
