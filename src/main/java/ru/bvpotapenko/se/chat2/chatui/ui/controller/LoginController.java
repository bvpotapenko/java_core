package ru.bvpotapenko.se.chat2.chatui.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.bvpotapenko.se.chat2.chatui.network.Client;
import ru.bvpotapenko.se.chat2.utils.Properties;
import ru.bvpotapenko.se.chat2.utils.SHAEncoder;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LoginController implements Initializable, PrimaryStageAware {
    private Scene mainChatScene;
    public TextField loginTextField;
    public TextField passwordTextField;
    public Button loginButton;
    private Stage primaryStage;
    private Client client;
    private Future<Client> futureClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.requestFocus();
    }

    public void login(ActionEvent event) {
        String hashPass = SHAEncoder.getSHA(passwordTextField.getText());
        if (hashPass == null || hashPass.isEmpty())
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = futureClient.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                new Thread(client).start();
                client.authorize(LoginController.this, loginTextField.getText(), hashPass);
            }
        }).start();
        loginTextField.setDisable(true);
        passwordTextField.setDisable(true);
        loginButton.setDisable(true);
    }

    public void switchScene(boolean isAuthOk) {
        if (isAuthOk) {
            primaryStage.setTitle("Chat | " + loginTextField.getText());
            primaryStage.setScene(mainChatScene);
            clearText();
        } else {
            loginTextField.setDisable(false);
            passwordTextField.setDisable(false);
            loginButton.setDisable(false);
        }
    }

    private void clearText() {
        loginTextField.clear();
        passwordTextField.clear();
    }

    public void setScene(Scene mainChatScene) {
        this.mainChatScene = mainChatScene;
    }

    public void setClient(Future<Client> futureClient) {
        this.futureClient = futureClient;
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
