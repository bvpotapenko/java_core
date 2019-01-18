package ru.bvpotapenko.se.chatui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.bvpotapenko.se.chatui.network.GenericSocketHandler;
import ru.bvpotapenko.se.chatui.ui.controller.ChatWindowController;
import ru.bvpotapenko.se.chatui.ui.controller.LoginController;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //init login
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent loginPane = loginLoader.load();
        Scene loginScene = new Scene(loginPane);
        loginScene.getStylesheets().add(Main.class.getResource("/login.css").toExternalForm());
        loginScene.setFill(Color.TRANSPARENT);

        //init chat
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/chatwindow.fxml"));
        Parent chatPane = chatLoader.load();
        Scene chatScene = new Scene(chatPane);
        chatScene.getStylesheets().add(Main.class.getResource("/chatwindow.css").toExternalForm());
        chatScene.setFill(Color.TRANSPARENT);

        LoginController loginController = loginLoader.getController();
        loginController.setScene(chatScene);

        ChatWindowController chatWindowController = chatLoader.getController();
        chatWindowController.setScene(loginScene);

        //Connection to Server
        GenericSocketHandler socket = new GenericSocketHandler(chatWindowController.chatTextAria);
        loginController.setSocket(socket);

        primaryStage.setTitle("Chat");
        /*primaryStage.initStyle(StageStyle.TRANSPARENT);*/
        primaryStage.setResizable(false);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
