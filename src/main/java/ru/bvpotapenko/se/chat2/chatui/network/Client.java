package ru.bvpotapenko.se.chat2.chatui.network;

import javafx.application.Platform;
import ru.bvpotapenko.se.chat2.chatui.Main;
import ru.bvpotapenko.se.chat2.chatui.ui.controller.LoginController;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.MessageFormat;

public class Client implements Runnable {
    private boolean isUserAuthorized = false;
    private ClientState socketState = ClientState.DISCONNECTED;

    private LoginController loginController;
    private Main mainApp;

    private Socket socket;
    private String clientName;

    private DataInputStream socketReader;
    private DataOutputStream socketWriter;
    private PrintStream outPrintStream;

    public Client(String host, int port, Main mainApp) {
        this.mainApp = mainApp;
        try {
            this.clientName = "";
            socketState = ClientState.CONNECTING;
            socket = new Socket(host, port);
            socketState = ClientState.CONNECTED;
            socketReader = new DataInputStream(socket.getInputStream());
            socketWriter = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            socketState = ClientState.DISCONNECTED;
            System.err.println(MessageFormat.format("Can\'t connect to Socket: {0}:{1}", host, port));
        }
    }

    public void sendMessage(String message) {
        if (socketState == ClientState.CONNECTED) {
            if (message == null || message.isEmpty()) return;
            System.out.println("LOG client is ready to send Quiet Message: " + message);
            sendQuietMessage(message);
            outPrintStream.println(clientName + ": " + message + "\n");
        }
    }


    public void sendQuietMessage(String message) {
        if (socketState == ClientState.CONNECTED) {
            if (message == null || message.isEmpty()) return;
            try {
                //We ensure the length of the message to parse on server side;
                byte[] arbytes = message.getBytes(Charset.forName("UTF-8"));
                socketWriter.writeInt(arbytes.length);
                socketWriter.write(arbytes);
                socketWriter.flush();
                System.out.println("LOG quiet message was sent from client: " + message);
            } catch (IOException e) {
                System.err.println("Message send error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("LOG client socket is listening");
            while (socketState == ClientState.CONNECTED) {
                String message = socketReader.readUTF();
                System.out.println("LOG client received a message: " + message);
                if (!isUserAuthorized && message.startsWith("/auth_ok")) {
                    isUserAuthorized = true;
                    System.out.println("LOG client auth confirmed");
                    //loginController.switchScene(true);
                    Platform.runLater(() ->mainApp.switchToChatScene(true));
                    continue;
                }else if(!isUserAuthorized){
                   //loginController.switchScene(false);
                    Platform.runLater(() ->mainApp.switchToChatScene(false));
                }
                outPrintStream.println(message);
                appendToHistory(message);
            }
            System.out.println("LOG client socket stopped listening");
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setOutPrintStream(PrintStream outPrintStream) {
        this.outPrintStream = outPrintStream;
    }

    public PrintStream getOutPrintStream() {
        return outPrintStream;
    }

    public Socket getSocket() {
        return socket;
    }

    public void authorize(LoginController controller, String login, String hashPassword) {
        this.loginController = controller;
        System.out.println("LOG: Auth attempt for " + login);
        if (socketState == ClientState.CONNECTED) {
            clientName = login;
            sendQuietMessage("/auth " + clientName + "&" + hashPassword);
        }
    }

    public boolean isUserAuthorized() {
        return isUserAuthorized;
    }

    public void closeConnection() {
        try {
            getOutPrintStream().flush();
            getOutPrintStream().close();
            getSocket().close();
            socketState = ClientState.DISCONNECTED;
        } catch (IOException ioe) {
            System.err.println("Socket close error: " + ioe.getMessage());
        }
    }

    public ClientState getSocketState() {
        return socketState;
    }


    private void appendToHistory(String line) {
        File history = new File(".\\history.txt");
        if (!history.exists()) {
            try {
                history.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedWriter br = new BufferedWriter(new FileWriter(history, true))) {
            br.write(line);
            br.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}