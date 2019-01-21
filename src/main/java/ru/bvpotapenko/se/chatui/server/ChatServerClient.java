package ru.bvpotapenko.se.chatui.server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*An entity of a connected client*/
public class ChatServerClient implements Runnable {

    private BufferedReader socketReader;
    private BufferedWriter socketWriter;
    private ChatServer server;

    // FIXME: 21-Jan-19  must be implemented as UserService with DB connection
    private String clientName = "";
    private boolean isAuthorized = false;

    public ChatServerClient(Socket socket, ChatServer server) throws IOException {
        this.server = server;
        socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = socketReader.readLine();
                // FIXME: 21-Jan-19 use JSON objects for messages
                Map<String, String> parsedMessage = parseMessage(message);
                if (parsedMessage == null) continue;
                System.out.println("LOG: \ncommand: " + parsedMessage.get("command") + "\n" +
                                    "user: " + parsedMessage.get("user") + "\n" +
                                    "message: " + parsedMessage.get("message"));
                switch (parsedMessage.get("command")) {
                    case "name":
                        clientName = parsedMessage.get("message");
                        if (isAuthorized)
                            sendMessage("/auth_ok");
                        break;
                    case "broadcast":
                        System.out.println("LOG case fired: \"broadcast\"");
                        server.sendBroadcast(clientName, parsedMessage.get("message"));
                        break;
                    case "u":
                        server.sendPrivateMessage(clientName, parsedMessage.get("user"), parsedMessage.get("message"));
                        break;
                    default:
                        server.sendPrivateMessage(clientName, clientName, "Unknown command");
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, String> parseMessage(String message) {
        if (message == null || message.isEmpty()) return null;
        System.out.println("LOG message to parse: " + message);
        Map<String, String> parsedMessage = new HashMap<>();

        Pattern pattern = Pattern.compile(
                "^[/](?<commandPrefix>\\w+)[&](?<commandSuffix>\\w+)\\s(?<mess1>.+)|" +
                        "^[/](?<comm>\\w+)\\s(?<mess2>.+)|" +
                        "^(?<mess3>[^/].*)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            if (matcher.group("commandPrefix") != null) {
                parsedMessage.put("command", matcher.group("commandPrefix"));
                parsedMessage.put("user", matcher.group("commandSuffix"));
                parsedMessage.put("message", matcher.group("mess1"));
            } else if (matcher.group("comm") != null) {
                parsedMessage.put("command", matcher.group("comm"));
                parsedMessage.put("message", matcher.group("mess2"));
            } else {
                parsedMessage.put("command", "broadcast");
                parsedMessage.put("message", matcher.group("mess3"));
            }
        }
        return parsedMessage;
    }

    public void sendMessage(String message) {
        try {
            socketWriter.write(message + "\n");
            socketWriter.flush();
            System.out.println("LOG message sent: " + message);
        } catch (IOException e) {
            System.err.println("Server send message error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            socketReader.close();
            socketWriter.flush();
            socketWriter.close();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.err.println("Stop client error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return clientName;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }
}
