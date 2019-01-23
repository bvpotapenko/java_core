package ru.bvpotapenko.se.chatui.network;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;

public class Client implements Runnable {
    private boolean isUserAuthorized = false;
    private ClientState socketState = ClientState.DISCONNECTED;

    private Socket socket;
    private String clientName;

    private DataInputStream socketReader;
    private DataOutputStream socketWriter;
    private PrintStream outPrintStream;

    public Client(String host, int port) {
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
                socketWriter.writeUTF(message + "\n");
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
                if (message.startsWith("/auth_ok")) {
                    isUserAuthorized = true;
                    System.out.println("LOG client auth confirmed");
                    continue;
                }
                outPrintStream.println(message);
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

    public void authorize(String login, String password) {
        System.out.println("LOG: Auth attempt for " + login);
        if (socketState == ClientState.CONNECTED) {
            clientName = login;
            new Thread(() -> {
                while (!isUserAuthorized) {
                    sendQuietMessage("/auth " + clientName+"&"+password);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        System.err.println("Client auth error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                System.out.println("Authorization success");
            }).start();
        } else
            System.out.println("LOG Auth attempt failed");
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
}
