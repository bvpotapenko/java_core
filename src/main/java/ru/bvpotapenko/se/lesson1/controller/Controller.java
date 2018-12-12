package ru.bvpotapenko.se.lesson1.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import ru.bvpotapenko.se.lesson1.console.Course;
import ru.bvpotapenko.se.lesson1.console.TeamMember;
import ru.bvpotapenko.se.lesson1.model.Track;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private ObservableList<TeamMember> teamMemberData = FXCollections.observableArrayList();
    @FXML
    private Button startButton;
    @FXML
    private Button newTeamButton;
    @FXML
    private Canvas canvas;
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<TeamMember, String> numberColumn;
    @FXML
    private TableColumn<TeamMember, String> nameColumn;
    @FXML
    private TableColumn<TeamMember, String> staminaColumn;
    @FXML
    private TableColumn<TeamMember, String> powerColumn;
    @FXML
    private TableColumn<TeamMember, String> failsColumn;

    private Course course;
    private List<Track> trackList;
    private final int TRACK_AMOUNT = 4;
    private final int TRACK_LENGTH = 340;
    private final int DISTANCE_BETWEEN_TRACKS = 60;
    private final int FIRST_TRACK_X = 30;
    private final int FIRST_TRACK_Y = 60;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initData();
        drawCourse();

        numberColumn.setCellValueFactory(new PropertyValueFactory<TeamMember, String>("number"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<TeamMember, String>("fullName"));
        staminaColumn.setCellValueFactory(new PropertyValueFactory<TeamMember, String>("stamina"));
        powerColumn.setCellValueFactory(new PropertyValueFactory<TeamMember, String>("power"));

        tableView.setItems(teamMemberData);
    }

    private void initData() {
        teamMemberData.add(new TeamMember("Semen", "Smirnov", 14, 6));
        teamMemberData.add(new TeamMember("John", "Black", 19, 4));
        teamMemberData.add(new TeamMember("Santa", "Claus", 20, 7));
        teamMemberData.add(new TeamMember());

        course = new Course();
        trackList = new ArrayList<>(TRACK_AMOUNT);
    }


    public void generateNewTeam(ActionEvent event) {
        for (int i = 0; i < 4; i++)
            teamMemberData.add(new TeamMember());
        tableView.getItems().remove(0, 4);
        tableView.setItems(teamMemberData);

    }

    private void drawCourse() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.strokeText("START", 10, 20);
        gc.strokeText("FINISH", 350, 20);

        for (int trackNumber =0; trackNumber < TRACK_AMOUNT; trackNumber++){
            int track_Y = FIRST_TRACK_Y + DISTANCE_BETWEEN_TRACKS * trackNumber;
            trackList.add(new Track(TRACK_LENGTH, FIRST_TRACK_X, track_Y, gc, course));
        }
    }

    /*private void drawSomething(GraphicsContext gc) {

        int obstaclesAmount = course.getObstacles().length;
        
        for (int i = 0; i < 4; i++) {

            drawStartFlag(gc, i);
            drawFinishFlag(gc, i);
            drawCourseLine(gc, i);

            //Add obstacles
            gc.setFill(Color.BURLYWOOD);

            for (int k = 0; k < obstaclesAmount; k++) {
                //тут надо что-то с координатами
                //иначе потом будет сложно перерисовывать
                //сохраняем координаты в массив
                int distanceBetweenObstacles = (340-30)/(obstaclesAmount+1);
                obstaclesPosition[k] = distanceBetweenObstacles*(k+1) + 30;
                gc.fillRect(obstaclesPosition[k],
                        60 + 60 * i - (course.getObstacles()[k]) * 5,
                        10,
                        course.getObstacles()[k] * 5);
            }
        }
    }

    private void drawStartFlag(GraphicsContext gc, int trackNumber){
        gc.setFill(Color.GREEN);
        gc.strokeLine(30, 30 + 60 * trackNumber, 30, 60 + 60 * trackNumber);
        gc.fillRect(30, 30 + 60 * trackNumber, 20, 10);
    }

    private void drawFinishFlag(GraphicsContext gc, int trackNumber){
        gc.setFill(Color.RED);
        gc.strokeLine(370, 30 + 60 * trackNumber, 370, 60 + 60 * trackNumber);
        gc.fillRect(350, 30 + 60 * trackNumber, 20, 10);
    }

    private void drawCourseLine(GraphicsContext gc, int trackNumber){
        gc.strokeLine(30, 60 + 60 * trackNumber, 370, 60 + 60 * trackNumber);
    }*/
}