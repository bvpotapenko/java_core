package ru.bvpotapenko.se.chatui.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/*Handles all the connections from clients*/
public class ChatServer implements Runnable {
    private ServerSocket serverSocket;
    // TODO: 20-Jan-19 translate to Map
    private Map<String, ChatServerClient> clients = new ConcurrentHashMap<>();

    public enum ChatServerState {
        ON,
        OFF
    }

    private ChatServerState serverState;

    public ChatServer(int port) {
        serverState = ChatServerState.OFF;
        try {
            serverSocket = new ServerSocket(port);
            serverState = ChatServerState.ON;
        } catch (IOException e) {
            System.err.println("Server can\'t connect to port: " + port);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (serverState == ChatServerState.ON) {
            try {
                ChatServerClient client = new ChatServerClient(serverSocket.accept(), this); // TODO: 20-Jan-19 process restart errors
                AuthService auth = new AuthService(clients, client);
                new Thread(auth).start();
                new Thread(client).start();
            } catch (IOException e) {
                if (serverState == ChatServerState.OFF) {
                    System.err.println("Connection terminated by a request: " + e.getMessage());
                } else {
                    System.err.println("Server add client error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void sendBroadcast(String sender, String message) {
        clients.forEach((name, client) -> {
            if (!name.equalsIgnoreCase(sender))
                client.sendMessage(message);
        });
    }

    public synchronized void sendPrivateMessage(String sender, String receiverName, String message) {
        clients.get(receiverName).sendMessage("Privately from " + sender + ": "+ message);
    }

    private void close() throws IOException {
        clients.forEach((name, client) -> client.stop());
        if (!serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public void stop() {
        try {
            serverState = ChatServerState.OFF;
            close();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.err.println("Stop server error: " + e.getMessage());
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public void reset(int port) {
        try {
            stop();
            serverSocket = new ServerSocket(port);
            serverState = ChatServerState.ON;
        } catch (IOException e) {
            System.err.println("On restart Server error:  can\'t connect to port: " + port);
            e.printStackTrace();
        }
    }
    public List<String> getUserList(){
        return new ArrayList(clients.keySet());
    }
    public List<String> getUserListState(){
        List<String> userListState = new ArrayList<>();
        for (Map.Entry<String, ChatServerClient> userEntry: clients.entrySet()){
            String user = userEntry.getKey() +
                    " - " +
                    userEntry.getValue().isAuthorized();
            userListState.add(user);
        }
        return userListState;
    }
}
