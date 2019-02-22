package ru.bvpotapenko.se.chatui.server;

import ru.bvpotapenko.se.chatui.server.Exceptions.AuthFailException;
import ru.bvpotapenko.se.chatui.server.Exceptions.AuthNameDoubled;
import ru.bvpotapenko.se.chatui.server.filters.ChatFilter;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*Handles all the connections from clients*/
public class ChatServer implements Runnable {
    private ServerSocket serverSocket;
    private Map<String, ChatServerClient> clients = new ConcurrentHashMap<>();
    private List<ChatFilter> filters = new LinkedList<>();

    public enum ChatServerState {
        ON,
        OFF
    }

    private ChatServerState serverState;

    public ChatServer(int port, String dbName) {
        serverState = ChatServerState.OFF;
        try {
            serverSocket = new ServerSocket(port);
            serverState = ChatServerState.ON;
            SQLHandler.connect(dbName);
        } catch (IOException e) {
            System.err.println("Server can\'t connect to port: " + port);
            e.printStackTrace();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Server can\'t connect to the data base: " + dbName);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (serverState == ChatServerState.ON) {
            try {
                ChatServerClient client = new ChatServerClient(serverSocket.accept(), this); // TODO: 20-Jan-19 process restart errors
                new Thread(client).start();
                new ClientTimeOutKiller(this, client);
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
        final String filteredMessage = filter(message);
        clients.forEach((name, client) -> {
            if (!name.equalsIgnoreCase(sender)) {
                client.sendMessage(clients.get(sender).getNick() + ": " + filteredMessage);
                System.out.println("LOG ChatServer sends a broadcast message to: " + name);
            }
        });
    }

    public synchronized void sendPrivateMessage(String sender, String receiverName, String message) {
        final String filteredMessage = filter(message);
        clients.get(receiverName).sendMessage("PM from " + clients.get(sender).getNick() + ": " + filteredMessage);
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
            SQLHandler.close();
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

    public List<String> getUserList() {
        return new ArrayList(clients.keySet());
    }

    public List<String> getUserListState() {
        List<String> userListState = new ArrayList<>();
        for (Map.Entry<String, ChatServerClient> userEntry : clients.entrySet()) {
            String user = userEntry.getKey() +
                    " - " +
                    userEntry.getValue().isAuthorized();
            userListState.add(user);
        }
        return userListState;
    }

    public synchronized void removeClient(ChatServerClient client) {
        try {
            clients.remove(client.getClientName());
            client.stop();
        } catch (Exception e) {
            System.err.println("Server remove client error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void addClient(String clientName, ChatServerClient client) throws AuthFailException {
        if (clients.containsKey(clientName)) {
            throw new AuthNameDoubled(clientName);
        }
        System.out.println("LOG try ChatServer.addClient: " + clientName);
        clients.put(clientName, client);
        try {
            client.setNick(SQLHandler.getClientNick(clientName));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sendBroadcast("", "New user: " + client.getNick() + " joined.");
    }

    public void addFilter(ChatFilter filter) {
        filters.add(filter);
        System.out.println("Filter is added!");
    }
    private String filter(String message){
        for (ChatFilter filter: filters)
            message = filter.filter(message);
        return message;
    }

    public void setNewNick(String clientName, String newNick) throws SQLException {
        SQLHandler.setClientNick(clientName, newNick);
    }
}
