package ru.bvpotapenko.se.chatui.server;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*An entity of a connected client*/
public class ChatServerClient implements Runnable {

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
        if(!socket.isClosed()) {
            System.out.println("LOG DEBUG STEP-3: A client tries to auth");
            waitForAuth();
            System.out.println("LOG DEBUG STEP-FINAL: A client is ready");
            waitForMessage();
        }
    }

    private void waitForAuth(){
        AuthService auth = new AuthService(server, this);
        System.out.println("LOG DEBUG STEP-4: A client starts auth service");
        new Thread(auth).start();
        System.out.println("LOG DEBUG STEP-4.afterAuthStart: A client sleeps for auth");
        while (!isAuthorized()){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.err.println("Server client auth fail: " + e.getMessage());
                e.printStackTrace();
            }
        }
        sendMessage("/auth_ok");
    }

    private void waitForMessage(){
        try {
            while (true) {
                System.out.println("LOG CSClient waits for a message");
                //String message = socketReader.readUTF();
                byte[] arbytes = new byte[socketReader.readInt()];
                for(int i = 0; i < arbytes.length; i++){
                    arbytes[i] = socketReader.readByte();
                }
                String message = new String(arbytes);
                // FIXME: 21-Jan-19 use JSON objects for messages
                Map<String, String> parsedMessage = parseMessage(message);
                if (parsedMessage == null) continue;
                System.out.println("LOG: \ncommand: " + parsedMessage.get("command") + "\n" +
                        "user: " + parsedMessage.get("user") + "\n" +
                        "message: " + parsedMessage.get("message"));
                switch (parsedMessage.get("command")) {
                    case "name":
                        //clientName = parsedMessage.get("message");
                        if (isAuthorized)
                            sendMessage("/auth_ok " + clientName);
                        break;
                    case "broadcast":
                        System.out.println("LOG case fired: \"broadcast\"");
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
                        if(newNick == null || newNick.isEmpty()){
                            sendMessage("Nick can't be empty");
                        }else{
                            try {
                                server.setNewNick(clientName, parsedMessage.get("message"));
                                this.nick = newNick;
                            } catch (SQLException e) {
                                sendMessage("Nick set error");
                                e.printStackTrace();
                            }
                        }
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

    /**Commands examples:
     * /u&nick_1 hello!
     * /auth u1&B78F576611EC06F96AF3CA654C22172A5D746C40
     * /nick newNick
     */

    private Map<String, String> parseMessage(String message) {
        if (message == null || message.isEmpty()) return null;
        System.out.println("LOG message to parse: " + message);
        Map<String, String> parsedMessage = new HashMap<>();

        Pattern pattern = Pattern.compile(
                        "^[/](?<commandPrefix>\\w+)[&](?<commandSuffix>\\w+([&]\\w+)*)\\s(?<mess1>.+)|" +
                        "^[/](?<comm>\\w+)\\s(?<mess2>.+)|" +
                     //   "^[/]\\w+([&](?<onlyCommand>[\\w\\d]+))|" +
                        "^[/](?<onlyCommand>[\\w\\d]+)|" +
                        "^(?<mess3>[^/].*)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            if (matcher.group("commandPrefix") != null) {
                parsedMessage.put("command", matcher.group("commandPrefix"));
                parsedMessage.put("user", matcher.group("commandSuffix"));
                parsedMessage.put("message", matcher.group("mess1"));
            } else if (matcher.group("comm") != null) {
                parsedMessage.put("command", matcher.group("comm"));
                parsedMessage.put("message", matcher.group("mess2"));
            } else if(matcher.group("onlyCommand") != null){
                parsedMessage.put("command", matcher.group("onlyCommand"));
            }else if(matcher.group("mess3") != null){
                parsedMessage.put("command", "broadcast");
                parsedMessage.put("message", matcher.group("mess3"));
            }else{
                parsedMessage.put("command", "unknownCommand");
            }
        }
        return parsedMessage;
    }

    public synchronized void sendMessage(String message) {
        try {
            socketWriter.writeUTF(message + "\n");
            socketWriter.flush();
            System.out.println("LOG message sent: " + message);
        } catch (IOException e) {
            System.err.println("Server send message error: " + e.getMessage());
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
            System.err.println("Stop client error: " + e.getMessage());
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
