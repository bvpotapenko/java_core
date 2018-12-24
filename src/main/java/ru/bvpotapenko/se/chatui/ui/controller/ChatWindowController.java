package ru.bvpotapenko.se.chatui.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatWindowController implements Initializable {
    public TextField messageTextField;
    public Button sendButton;
    public TextArea chatTextAria;
    private Scene loginScene;
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
        chatTextAria.setEditable(false);
        messageTextField.requestFocus();
    }
    public void logout(ActionEvent event) {
        clearTextFields();
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        primaryStage.setScene(loginScene);
    }


    public void setScene(Scene loginScene){
        this.loginScene = loginScene;
    }

    public void quit(ActionEvent event) {
        System.exit(0);
    }

    public void sendMessage(ActionEvent event) {
        String newMessage = "User: " +
                messageTextField.getText() +
                "\r\n";
        chatTextAria.appendText(newMessage);
        messageTextField.clear();
    }

    private void clearTextFields(){
        chatTextAria.clear();
        messageTextField.clear();
    }
}
