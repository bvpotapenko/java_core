package ru.bvpotapenko.se.chatui.network;

import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GenericSocketHandler implements Runnable {
    private static TextArea chatTextAria;

    private final String SERVER_HOST = "127.0.0.1";
    private final int SERVER_PORT = 8791;
    private static boolean isUserAuthorized = false;

    private static Socket socket;
    private static DataOutputStream out;
    private static DataInputStream in;

    public GenericSocketHandler(TextArea chatTextAria) {
        GenericSocketHandler.chatTextAria = chatTextAria;
    }

    public static void sendMessage(String message) {
        if (message  == null || message.isEmpty()) return;
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void authorize(String login, String password) {
        isUserAuthorized = true;
        System.out.println("Authorization success");
    }
    public static boolean isUserAuthorized(){
        return isUserAuthorized;
    }
    @Override
    public void run() {
        System.out.println("Socket thread started");
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("/HELLO!");
            out.flush();
            while (isUserAuthorized) {
                if (in.available() > 0) {
                    String message =  in.readUTF();
                    if(message.equalsIgnoreCase("/end")) break;
                    chatTextAria.appendText("Server: " +  message + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Terminated by Server command \"/end\"");
        System.exit(0);
    }
}
