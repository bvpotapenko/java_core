package ru.bvpotapenko.se.chatui.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private final String QUIT_COMMAND = "/end";
    private final String CONNECTED_MESSAGE = "Hello from Server";
    private final String SERVER_IS_ON_MESSAGE = "Server is awaiting...";
    private final String SERVER_IS_OFF_MESSAGE = "Server shuts down.";
    private final String HAS_A_CLIENT_MESSAGE = "Client is connected.";
    private final String SERVER_SHUT_DOWN_BY_COMMAND_MESSAGE = "Server has been terminated by the \"" +
            QUIT_COMMAND + "\" command.";

    private final int PORT = 8791;
    private ServerSocket server;
    private Socket client;


    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        try {
            server = new ServerSocket(PORT);
            System.out.println(SERVER_IS_ON_MESSAGE);
            client = server.accept();
            System.out.println(HAS_A_CLIENT_MESSAGE);
            new Thread(this::sendMessage).start();
            new Thread(this::processClientMessage).start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void sendMessage() {
        Scanner scanner = null;
        DataOutputStream out = null;
        try {
            scanner = new Scanner(System.in);
            out = new DataOutputStream(client.getOutputStream());
            while (true) {
                if (scanner.hasNext()) {
                    String message = scanner.nextLine();
                    if (QUIT_COMMAND.equalsIgnoreCase(message)) {
                        break;
                    }
                    out.writeUTF(message);
                    out.flush();
                    System.err.println("SENT: " + message);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (scanner != null) scanner.close();
                if (out != null) out.close();
                client.close();
                server.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        System.exit(0);
    }

    private void processClientMessage() {
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            while (true) {
                if (in.available() > 0) {
                    String message = in.readUTF();
                    System.out.println("Client: " + message + "\n");
                    if (QUIT_COMMAND.equalsIgnoreCase(message)) {
                        out.writeUTF("<ECHO> " + message + " -> " + SERVER_IS_OFF_MESSAGE);
                        out.flush();
                        System.out.println(SERVER_SHUT_DOWN_BY_COMMAND_MESSAGE);
                        break;
                    }
                    if ("/HELLO!".equalsIgnoreCase(message)) {
                        out.writeUTF(CONNECTED_MESSAGE);
                        out.flush();
                        System.out.println("Client is ready.");
                        continue;
                    }
                    out.writeUTF("<ECHO> " + message);
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                client.close();
                server.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        System.exit(0);
    }
}
