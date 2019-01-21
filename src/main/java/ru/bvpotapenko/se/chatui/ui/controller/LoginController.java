package ru.bvpotapenko.se.chatui.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.bvpotapenko.se.chatui.network.Client;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable, PrimaryStageAware {
    private Scene mainChatScene;
    public TextField loginTextField;
    public TextField passwordTextField;
    public Button loginButton;
    private Stage primaryStage;
    private Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.requestFocus();
    }

    public void login(ActionEvent event) {
        client.authorize(loginTextField.getText(), passwordTextField.getText());
        if (client.isUserAuthorized()) {
            new Thread(client).start();
            primaryStage.setTitle("Chat | " + loginTextField.getText());
        }
        clearText();
        primaryStage.setScene(mainChatScene);
    }

    private void clearText() {
        loginTextField.clear();
        passwordTextField.clear();
    }

    public void setScene(Scene mainChatScene) {
        this.mainChatScene = mainChatScene;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
