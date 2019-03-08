package ru.bvpotapenko.se.chat2.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.bvpotapenko.se.chat2.server.Exceptions.AuthFailException;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*An entity of a connected client*/
public class ChatServerClient implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(ChatServerClient.class);

    private DataInputStream socketReader;
    private DataOutputStream socketWriter;
    private ChatServer server;
    private Socket socket;

    private String clientName = "";
    private String nick = "";
    private boolean isAuthorized = false;

    public ChatServerClient(Socket socket, ChatServer server) throws IOException {
        this.server = server;
        this.socket = socket;
        socketReader = new DataInputStream(this.socket.getInputStream());
        socketWriter = new DataOutputStream(this.socket.getOutputStream());
    }

    @Override
    public void run() {
        if (!socket.isClosed()) {
            LOGGER.debug("LOG DEBUG STEP-3: A client tries to auth");
            try {
                waitForAuth();
                LOGGER.debug("LOG DEBUG STEP-FINAL: A client is ready");

            } catch (InterruptedException | AuthFailException e) {
                LOGGER.error(e.getMessage());
                return;
            }
            if (isAuthorized()) {
                waitForMessage();
            }
        }
    }

    private void waitForAuth() throws InterruptedException, AuthFailException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        AuthService auth = new AuthService(server, this);
        LOGGER.debug("LOG DEBUG STEP-4: A client starts auth service");
        Future<Boolean> futureIsAuth = executorService.submit(auth);
        LOGGER.debug("LOG DEBUG STEP-4.afterAuthStart: A client sleeps for auth");
        try {
            setAuthorized(futureIsAuth.get());
        } catch (ExecutionException e) {
            LOGGER.error( e.getMessage());
        }
        if (isAuthorized) {
            LOGGER.debug("LOG DEBUG STEP-12.authOK!: AUTH tries to add a client");
            server.addClient(this);
            sendMessage("/auth_ok");
        }
        executorService.shutdown();
    }

    private void waitForMessage() {
        try {
            while (true) {
                LOGGER.debug("LOG CSClient waits for a message");
                byte[] arbytes = new byte[socketReader.readInt()];
                for (int i = 0; i < arbytes.length; i++) {
                    arbytes[i] = socketReader.readByte();
                }
                String message = new String(arbytes);
                // FIXME: 21-Jan-19 use JSON objects for messages
                Map<String, String> parsedMessage = parseMessage(message);
                if (parsedMessage == null) continue;
                LOGGER.debug("LOG: \ncommand: " + parsedMessage.get("command") + "\n" +
                        "user: " + parsedMessage.get("user") + "\n" +
                        "message: " + parsedMessage.get("message"));
                switch (parsedMessage.get("command")) {
                    case "name":
                        if (isAuthorized)
                            sendMessage("/auth_ok " + clientName);
                        break;
                    case "broadcast":
                        LOGGER.debug("LOG case fired: \"broadcast\"");
                        server.sendBroadcast(clientName, parsedMessage.get("message"));
                        break;
                    case "u":
                        server.sendPrivateMessageByNick(clientName, parsedMessage.get("user"), parsedMessage.get("message"));
                        break;
                    case "ul":
                        sendMessage("Connected users: " + server.getNickList());
                        break;
                    case "nick":
                        String newNick = parsedMessage.get("message");
                        if (newNick == null || newNick.isEmpty()) {
                            sendMessage("Nick can't be empty");
                        } else {
                            try {
                                server.setNewNick(clientName, parsedMessage.get("message"));
                                this.nick = newNick;
                            } catch (SQLException e) {
                                sendMessage("Nick set error");
                                LOGGER.error(e.getMessage());
                            }
                        }
                        break;
                    default:
                        server.sendPrivateMessage(clientName, clientName, "Unknown command");
                }
            }
        } catch (IOException e) {
            LOGGER.error("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Commands examples:
     * Private message: /u&nick_1 hello!
     * Login: /auth u1&B78F576611EC06F96AF3CA654C22172A5D746C40
     * Set new nick name: /nick newNick
     */

    private Map<String, String> parseMessage(String message) {
        if (message == null || message.isEmpty()) return null;
        System.out.println("LOG message to parse: " + message);
        Map<String, String> parsedMessage = new HashMap<>();

        Pattern pattern = Pattern.compile(
                "^[/](?<commandPrefix>\\w+)[&](?<commandSuffix>\\w+([&]\\w+)*)\\s(?<data1>.+)|" +
                        "^[/](?<singleComm>\\w+)\\s(?<data2>.+)|" +
                        "^[/](?<onlyCommand>[\\w\\d]+)|" +
                        "^(?<data3>[^/].*)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            if (matcher.group("commandPrefix") != null) {
                parsedMessage.put("command", matcher.group("commandPrefix"));
                parsedMessage.put("user", matcher.group("commandSuffix"));
                parsedMessage.put("message", matcher.group("data1"));
            } else if (matcher.group("singleComm") != null) {
                parsedMessage.put("command", matcher.group("singleComm"));
                parsedMessage.put("message", matcher.group("data2"));
            } else if (matcher.group("onlyCommand") != null) {
                parsedMessage.put("command", matcher.group("onlyCommand"));
            } else if (matcher.group("data3") != null) {
                parsedMessage.put("command", "broadcast");
                parsedMessage.put("message", matcher.group("data3"));
            } else {
                parsedMessage.put("command", "unknownCommand");
            }
        }
        return parsedMessage;
    }

    public synchronized void sendMessage(String message) {
        try {
            socketWriter.writeUTF(message + "\n");
            socketWriter.flush();
            LOGGER.debug("LOG message sent: " + message);
        } catch (IOException e) {
            LOGGER.error("Server send message error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void stop() {
        try {
            socketWriter.writeUTF("/stop");
            socketReader.close();
            socketWriter.flush();
            socketWriter.close();
            socket.close();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            LOGGER.error("Stop client error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized String getClientName() {
        return clientName;
    }

    public synchronized void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public synchronized boolean isAuthorized() {
        return isAuthorized;
    }

    public boolean isUp() {
        return !socket.isClosed();
    }

    public DataInputStream getSocketReader() {
        return socketReader;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
