package ru.bvpotapenko.se.chatui.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.bvpotapenko.se.chatui.network.GenericSocketHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private Scene mainChatScene;
    public TextField loginTextField;
    public TextField passwordTextField;
    public Button loginButton;
    private GenericSocketHandler socket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.requestFocus();
    }

    public void login(ActionEvent event) {
        GenericSocketHandler.authorize(loginTextField.getText(), passwordTextField.getText());
        if (GenericSocketHandler.isUserAuthorized())
            new Thread(socket).start();
        clearText();
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(mainChatScene);
    }

    private void clearText() {
        loginTextField.clear();
        passwordTextField.clear();
    }

    public void setScene(Scene mainChatScene) {
        this.mainChatScene = mainChatScene;
    }

    public void quit(ActionEvent event) {
        System.exit(0);
    }

    public void setSocket(GenericSocketHandler socket) {
        this.socket = socket;
    }
}
