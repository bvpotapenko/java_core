package ru.bvpotapenko.se.lesson1.controller;

import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import ru.bvpotapenko.se.lesson1.console.Course;
import ru.bvpotapenko.se.lesson1.console.TeamMember;
import ru.bvpotapenko.se.lesson1.model.TeamMemberToken;
import ru.bvpotapenko.se.lesson1.model.Track;
import ru.bvpotapenko.se.lesson1.model.obstacle.Obstacle;
import ru.bvpotapenko.se.lesson1.model.obstacle.ObstacleState;

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
    private Group group;
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
    private List<TeamMemberToken> teamMemberTokenList;
    private final int TRACK_AMOUNT = 4;
    private final int TRACK_LENGTH = 340;
    private final int DISTANCE_BETWEEN_TRACKS = 60;
    private final int FIRST_TRACK_X = 30;
    private final int FIRST_TRACK_Y = 60;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initData();
        drawCourse(false);

        numberColumn.setCellValueFactory(new PropertyValueFactory<TeamMember, String>("number"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<TeamMember, String>("fullName"));
        staminaColumn.setCellValueFactory(new PropertyValueFactory<TeamMember, String>("stamina"));
        powerColumn.setCellValueFactory(new PropertyValueFactory<TeamMember, String>("power"));
        failsColumn.setCellValueFactory(new PropertyValueFactory<TeamMember, String>("failsMade"));

        tableView.setItems(teamMemberData);
        drawTeam();
    }

    private void initData() {
        teamMemberData.add(new TeamMember("Semen", "Smirnov", 14, 6));
        teamMemberData.add(new TeamMember("John", "Black", 19, 4));
        teamMemberData.add(new TeamMember("Santa", "Claus", 20, 7));
        teamMemberData.add(new TeamMember());

        course = new Course();
        trackList = new ArrayList<>(TRACK_AMOUNT);
        teamMemberTokenList = new ArrayList<>(TRACK_AMOUNT);
    }

    public void generateNewTeam(ActionEvent event) {
        drawCourse(true);
        for (int i = 0; i < 4; i++)
            teamMemberData.add(new TeamMember());
        tableView.getItems().remove(0, 4);
        tableView.setItems(teamMemberData);
        drawTeam();
    }

    private void drawCourse(boolean isRandom) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,400,400);
        trackList.clear();
        gc.strokeText("START", 10, 20);
        gc.strokeText("FINISH", 350, 20);
        if(isRandom) course.generateRandomObstacles();
        for (int trackNumber = 0; trackNumber < TRACK_AMOUNT; trackNumber++) {
            int track_Y = FIRST_TRACK_Y + DISTANCE_BETWEEN_TRACKS * trackNumber;
            trackList.add(new Track(TRACK_LENGTH, FIRST_TRACK_X, track_Y, gc, course));
        }
    }

    private void drawTeam() {
        teamMemberTokenList.forEach(TeamMemberToken::removeToken);
        teamMemberTokenList.clear();
        for (int trackNumber = 0; trackNumber < TRACK_AMOUNT; trackNumber++) {
            TeamMember tm = teamMemberData.get(trackNumber);
            int track_Y = FIRST_TRACK_Y + DISTANCE_BETWEEN_TRACKS * trackNumber;
            TeamMemberToken teamMemberToken =
                    new TeamMemberToken(FIRST_TRACK_X, track_Y, Color.ORANGE, tm, trackList.get(trackNumber), group);
            teamMemberTokenList.add(teamMemberToken);
            teamMemberToken.drawShape();
        }
    }

    public void doIt() {
        trackList.forEach(x -> animateTrack(x));
    }

    private void animateTrack(Track track) {
        List<TranslateTransition> translateList = new ArrayList<>();
        //Get a token for current track
        TeamMemberToken tokenToAnimate = teamMemberTokenList.stream()
                .filter(x -> track.equals(x.getTrack()))
                .findAny()
                .orElse(null);
        TeamMember teamMember = tokenToAnimate.getTeamMember();
        int failsOnTrack = 0;
        int stamina = teamMember.getStamina();
        for (Obstacle o : track.getTrackObstacles().getObstacles()) {
            int oX = o.getX();
            if(o.getObstacleHeight() > teamMember.getPower() || o.getObstacleHeight() > stamina ) {
                failsOnTrack++;
                o.setState(ObstacleState.FAILED);
            }else o.setState(ObstacleState.PASSED);
            //animate.
            TranslateTransition ttRun = new TranslateTransition(Duration.millis(stamina > 1 ? 450*15/stamina : 0));
            TranslateTransition ttJumpUp = new TranslateTransition(Duration.millis(stamina > 1 ? 250*15/stamina : 0));
            TranslateTransition ttJumpDown = new TranslateTransition(Duration.millis(stamina > 1 ? 250*15/stamina : 0));
            ttRun.setToX(oX - 30);
            int jumpHeight = getMin(stamina, teamMember.getPower());
            ttJumpUp.setToY(-1 * jumpHeight*o.getOBSTACLE_HEIGHT_MULTIPLIER());
            ttJumpDown.setToY(0);

            if(stamina >0 ) {
                translateList.add(ttRun);
                translateList.add(ttJumpUp);
                translateList.add(ttJumpDown);
            }
            stamina -= o.getObstacleHeight() - teamMember.getPower()/4; //Fatigue simulator
        }
        //updating a table with fails and stamina
        teamMember.setFailsMade(failsOnTrack);
        teamMember.setStamina(stamina);
        int index = teamMemberData.indexOf(teamMember);
        teamMemberData.set(index, teamMember);
        tableView.refresh();

        //run extra mile
        TranslateTransition ttRun = new TranslateTransition(Duration.millis(stamina > 1 ? 450*10/stamina : 0));
        ttRun.setToX(320);
        if (stamina > 0) translateList.add(ttRun);
        SequentialTransition seqT = new SequentialTransition(
                tokenToAnimate.getStack(),
                translateList.toArray(new TranslateTransition[0]));
        //Set color for winners and losers
        FillTransition ft = new FillTransition(Duration.millis(200));
        ft.setFromValue(Color.YELLOW);
        ft.setShape((Circle)tokenToAnimate.getStack().getChildren().get(0));
        if(failsOnTrack <= course.getMaxFailsAllowed() && stamina > 0)
            ft.setToValue(Color.GREEN);
        else
            ft.setToValue(Color.RED);
        seqT.getChildren().add(ft);
        seqT.play();
    }

    private int getMin(int a, int b){
        return a < b ? a : b;
    }
}