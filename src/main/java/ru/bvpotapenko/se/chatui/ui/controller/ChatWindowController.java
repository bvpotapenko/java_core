package ru.bvpotapenko.se.chatui.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ChatWindowController implements Initializable {
    public TextField messageTextField;
    public Button sendButton;
    public TextArea chatTextAria;
    private Scene loginScene;

    private final String SERVER_HOST = "localhost";
    private final int SERVER_PORT = 8791;

    private Socket socket;
    private Scanner scanner;
    private PrintWriter pw;

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
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            scanner = new Scanner(socket.getInputStream());
            pw = new PrintWriter(socket.getOutputStream());
            pw.print("Hi, Server! It's Client!");
            pw.flush();
            new Thread(this::receiveMessage).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void logout(ActionEvent event) {
        clearTextFields();
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(loginScene);
    }
    public void setScene(Scene loginScene) {
        this.loginScene = loginScene;
    }

    public void quit(ActionEvent event) {
        System.exit(0);
    }

    public void sendMessage() {
        String message = messageTextField.getText();
        pw.print(message);
        pw.flush();
        chatTextAria.appendText( "User: " + message + "\r\n" );
        messageTextField.clear();
    }

    private void receiveMessage() {
        while (true) {
            if (scanner.hasNext()) {
                String message = "Server: " + scanner.nextLine() + "\r\n";
                chatTextAria.appendText(message);
            }
        }
    }

    private void clearTextFields() {
        chatTextAria.clear();
        messageTextField.clear();
    }
}
