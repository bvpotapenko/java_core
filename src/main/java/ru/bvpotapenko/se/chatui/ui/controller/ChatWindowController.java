package ru.bvpotapenko.se.chatui.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.bvpotapenko.se.chatui.network.Client;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.Stack;

public class ChatWindowController implements Initializable, PrimaryStageAware {
    public TextField messageTextField;
    public Button sendButton;
    public TextArea chatTextAria;
    private Scene loginScene;
    private PrintStream printStream;
    private Stage primaryStage;
    private Client client;

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
        printStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                chatTextAria.appendText(String.valueOf((char) b));
            }
        });
        loadLastMessages();
    }

    private void loadLastMessages(){
        /**
         * 1. open file
         * 2. if no file -> create -> return
         * 3. if not empty
         *  3.1. while <100
         *         read from end to stack
         *         append to TextArea
         * 4. close file.
         */
        File history = new File(".\\history.txt");
        if(!history.exists()){
            try {
                history.createNewFile();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Stack<String> stack = new Stack<>();
        String line;
        try (ReversedLinesFileReader object = new ReversedLinesFileReader(history, Charset.forName("UTF-8"))){
            while (stack.size() <= 100 && (line = object.readLine())!= null)
            stack.push(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (stack.size()>0){
            chatTextAria.appendText(stack.pop() + "\n");
        }
    }

    public void sendMessage(ActionEvent event) {
        String message = messageTextField.getText();
        if (message == null || message.isEmpty()) return;
        client.sendMessage(message);
       /* chatTextAria.appendText("You said: " + message + "\n");*/
        messageTextField.clear();
    }


    public void logout(ActionEvent event) {
        client.closeConnection();
        clearTextFields();
        primaryStage.setTitle("Chat | Login");
        primaryStage.setScene(loginScene);
    }

    public void setScene(Scene loginScene) {
        this.loginScene = loginScene;
    }

    public void quit(ActionEvent event) {
        System.exit(0);
    }

    private void clearTextFields() {
        chatTextAria.clear();
        messageTextField.clear();
    }

    public void setClient(Client client) {
        this.client = client;
        client.setOutPrintStream(printStream);
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
