package ru.bvpotapenko.se.chat2.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.bvpotapenko.se.chat2.server.Exceptions.AuthFailException;
import ru.bvpotapenko.se.chat2.server.Exceptions.AuthNameDoubled;
import ru.bvpotapenko.se.chat2.server.filters.ChatFilter;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/*Handles all the connections from clients*/
public class ChatServer implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(ChatServer.class);
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
            LOGGER.error("Server can\'t connect to port: " + port);
            e.printStackTrace();
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("Server can\'t connect to the data base: " + dbName);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        while (serverState == ChatServerState.ON) {
            try {
                LOGGER.debug("LOG DEBUG STEP-1: Wait for a client");
                ChatServerClient client = new ChatServerClient(serverSocket.accept(), this); // TODO: 20-Jan-19 process restart errors
                LOGGER.debug("LOG DEBUG STEP-2: Client hooked up");
                executorService.execute(client);
                new ClientTimeOutKiller(this, client);
            } catch (IOException e) {
                if (serverState == ChatServerState.OFF) {
                    LOGGER.error("Connection terminated by a request: " + e.getMessage());
                } else {
                    LOGGER.error("Server add client error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        executorService.shutdown();
    }

    public synchronized void sendBroadcast(String sender, String message) {
        final String filteredMessage = clients.get(sender).getNick() + ": " + filter(message);
        LOGGER.debug("sender: " + sender);
        LOGGER.debug("LOG clients" + clients);
        clients.forEach((name, client) -> {
            if (!name.equalsIgnoreCase(sender)) {
                client.sendMessage(filteredMessage);
                LOGGER.debug("LOG ChatServer sends a broadcast message to: " + name);
            }
        });
    }

    public synchronized void sendBroadcastByNick(String senderNick, String message) {
        String senderName = getNickByName(senderNick);
        sendBroadcast(senderName, senderNick + ": " + message);
    }

    public String getNickByName(String name) {
        for (Map.Entry<String, ChatServerClient> e : clients.entrySet()) {
            if (e.getValue() != null
                    && e.getValue().getClientName().equals(name)) {
                return e.getValue().getNick();
            }
        }
        return "";
    }

    public synchronized void sendPrivateMessage(String sender, String receiverName, String message) {
        final String filteredMessage = filter(message);
        clients.get(receiverName).sendMessage("PM from " + clients.get(sender).getNick() + ": " + filteredMessage);
    }

    public synchronized void sendPrivateMessageByNick(String sender, String receiverNick, String message) {
        String receiverName;
        for (Map.Entry<String, ChatServerClient> e : clients.entrySet()) {
            if (e.getValue() != null
                    && e.getValue().getNick().equals(receiverNick)) {
                receiverName = e.getValue().getClientName();
                sendPrivateMessage(sender, receiverName, message);
            }
        }
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
            LOGGER.error("Stop server error: " + e.getMessage());
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
            LOGGER.error("On restart Server error:  can\'t connect to port: " + port);
            e.printStackTrace();
        }
    }

    public List<String> getUserList() {
        return new ArrayList(clients.keySet());
    }

    public ArrayList getNickList() {
        return new ArrayList(clients
                .entrySet()
                .stream()
                .map(e -> e.getValue().getNick())
                .collect(Collectors.toList()));
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
            LOGGER.error("Server remove client error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void addClient(ChatServerClient client) throws AuthFailException {
        LOGGER.debug("LOG DEBUG STEP-13: SERVER add client: " + client.getClientName());
        if (clients.containsKey(client.getClientName())) {
            throw new AuthNameDoubled(client.getClientName());
        }
        LOGGER.debug("LOG try ChatServer.addClient: " + client.getClientName());
        clients.put(client.getClientName(), client);
        try {
            client.setNick(SQLHandler.getClientNick(client.getClientName()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sendBroadcast(client.getClientName(), "New user: " + client.getNick() + " joined.");
    }

    public void addFilter(ChatFilter filter) {
        filters.add(filter);
        LOGGER.debug("Filter is added!");
    }

    private String filter(String message) {
        for (ChatFilter filter : filters)
            message = filter.filter(message);
        return message;
    }

    public void setNewNick(String clientName, String newNick) throws SQLException {
        SQLHandler.setClientNick(clientName, newNick);
    }
}
