package ru.bvpotapenko.se.chat2.chatui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.bvpotapenko.se.chat2.chatui.network.Client;
import ru.bvpotapenko.se.chat2.chatui.network.ClientConnectionService;
import ru.bvpotapenko.se.chat2.chatui.ui.controller.ChatWindowController;
import ru.bvpotapenko.se.chat2.chatui.ui.controller.LoginController;
import ru.bvpotapenko.se.chat2.utils.Properties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Main extends Application {
    //private Client client;
    LoginController loginController;

    public void switchToChatScene(boolean isOk){
        loginController.switchScene(isOk);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ClientConnectionService ccs = new ClientConnectionService(this);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Client> futureClient = executorService.submit(ccs);
        executorService.shutdown();

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

        loginController = loginLoader.getController();
        loginController.setScene(chatScene);
        loginController.setClient(futureClient);
        loginController.setPrimaryStage(primaryStage);

        ChatWindowController chatWindowController = chatLoader.getController();
        chatWindowController.setScene(loginScene);
        chatWindowController.setClient(futureClient);
        chatWindowController.setPrimaryStage(primaryStage);
        chatWindowController.initBackgroundTasks();


        primaryStage.setTitle("Chat | Login");
        // FIXME: 21-Jan-19 handle close operation
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        /*primaryStage.initStyle(StageStyle.TRANSPARENT);*/
        primaryStage.setResizable(false);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
