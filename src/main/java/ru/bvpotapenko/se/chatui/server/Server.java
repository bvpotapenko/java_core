package ru.bvpotapenko.se.chatui.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private final  String QUIT_COMMAND = "end";
    private final  String CONNECTED_MESSAGE = "You are connected to the server";
    private final  String SERVER_IS_ON_MESSAGE = "Server is awaiting...";
    private final  String SERVER_IS_OFF_MESSAGE = "Server shuts down.";
    private final  String HAS_A_CLIENT_MESSAGE = "Client is connected.";
    private final  String SERVER_SHUT_DOWN_BY_COMMAND_MESSAGE = "Server has been terminated by the \"" +
            QUIT_COMMAND + "\" command.";
    private final int PORT = 8791;
    public static void main(String[] args) {
        new Server();
    }
    private Server() {
        ServerSocket server = null;
        Socket client;
        try {
            server = new ServerSocket(PORT);
            System.out.println(SERVER_IS_ON_MESSAGE);
            client = server.accept();
            System.out.println(HAS_A_CLIENT_MESSAGE);
            new Thread(() -> sendMessage(client)).start();
           /*new Thread(() -> processClientMessage(client)).start();*/
            processClientMessage(client);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }finally {
            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void sendMessage(Socket client) {
        try {
            Scanner consoleScanner = new Scanner(System.in);
            PrintWriter pw = new PrintWriter(client.getOutputStream());
            System.out.print("Server: " );
            while (true) {
               if(consoleScanner.hasNext()) {
                    String message = consoleScanner.nextLine();
                    if (QUIT_COMMAND.equalsIgnoreCase(message)) {
                        break;
                    }
                    pw.println(message);
                    pw.flush();
                   System.out.print("\nServer: " );
               }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.exit(0);
    }

    private void processClientMessage(Socket client) {
        try {
            Scanner clientScanner = new Scanner(client.getInputStream());
            PrintWriter pw = new PrintWriter(client.getOutputStream());
            pw.println(CONNECTED_MESSAGE);
            pw.flush();
            while (true) {
               // if(clientScanner.hasNext()) {
                    String message = clientScanner.nextLine();
                    if (QUIT_COMMAND.equalsIgnoreCase(message)) {
                        pw.print("<ECHO> " + message + " -> " + SERVER_IS_OFF_MESSAGE);
                        pw.flush();
                        System.out.println(SERVER_SHUT_DOWN_BY_COMMAND_MESSAGE);
                        break;
                    }
                    System.out.println("Client: " + message);
                    pw.println("<ECHO> " + message);
                    pw.flush();
                //}
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.exit(0);
    }
}
