package ru.bvpotapenko.se.chatui.server;

import ru.bvpotapenko.se.chatui.utils.Properties;

import java.util.Scanner;


public class Server {
    public static void main(String[] args) {
        ChatServer server = new ChatServer(Properties.PORT);
        new Thread(server).start();
        System.out.println("Server started, press \"q\" for exit or \"r\" to restart");
        Scanner scanner = new Scanner(System.in);
        String command = "";
        while (!"q".equalsIgnoreCase(command)) {
            command = scanner.nextLine();
            switch (command) {
                case "q":
                    server.stop();
                    System.out.println("Server terminated.");
                    break;
                case "r":
                    server.reset(Properties.PORT);
                    new Thread(server).start();
                    System.out.println("Server restarted.");
                    break;
                case "ul":
                    System.out.println(server.getUserListState());
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    System.out.println("press \"q\" for exit or \"r\" to restart");
            }
        }
    }
}
