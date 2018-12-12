package ru.bvpotapenko.se.lesson1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        //Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        loader.setLocation(getClass().getResource("/main.fxml"));
        Parent root = loader.load();
      //  Controller controller = loader.getController();

       // RaceAnimator animator = new RaceAnimator((BorderPane)root, controller.getTeamMemberTokenList(), controller.getTeamMemberData());

        primaryStage.setTitle("The Race");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
