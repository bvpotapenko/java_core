package ru.bvpotapenko.se.chatui.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private Scene mainChatScene;
    public TextField loginTextField;
    public TextField passwordTextField;
    public Button loginButton;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.requestFocus();
    }

    public void login(ActionEvent event) {
        clearText();
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        primaryStage.setScene(mainChatScene);
    }

    private void clearText(){
        loginTextField.clear();
        passwordTextField.clear();
    }

    public void setScene(Scene mainChatScene){
        this.mainChatScene = mainChatScene;
    }

    public void quit(ActionEvent event) {
        System.exit(0);
    }

}
