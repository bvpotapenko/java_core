package ru.bvpotapenko.se.chatui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.bvpotapenko.se.chatui.network.Client;
import ru.bvpotapenko.se.chatui.ui.controller.ChatWindowController;
import ru.bvpotapenko.se.chatui.ui.controller.LoginController;
import ru.bvpotapenko.se.chatui.utils.Properties;


public class Main extends Application {
    private Client client;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Connection to Server
        setUpClient();

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
        loginController.setClient(client);
        loginController.setPrimaryStage(primaryStage);

        ChatWindowController chatWindowController = chatLoader.getController();
        chatWindowController.setScene(loginScene);
        chatWindowController.setClient(client);
        chatWindowController.setPrimaryStage(primaryStage);


        primaryStage.setTitle("Chat | Login");
        // FIXME: 21-Jan-19 handle close operation
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        /*primaryStage.initStyle(StageStyle.TRANSPARENT);*/
        primaryStage.setResizable(false);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
    private void setUpClient(){
        client = new Client(Properties.HOST, Properties.PORT);
        client.setOutPrintStream(System.out);
        if (client.getOutPrintStream() == null)
            System.out.println("LOG in Main.setUpClient() outPrintStream == null");
    }
    public static void main(String[] args) {
        launch(args);
    }
}
