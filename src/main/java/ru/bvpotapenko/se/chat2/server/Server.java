package ru.bvpotapenko.se.chat2.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.bvpotapenko.se.chat2.utils.Properties;
import ru.bvpotapenko.se.chat2.server.filters.JavaOnlyFilter;

import java.util.Scanner;
import java.util.concurrent.Executors;


public class Server {
    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    public static void main(String[] args) {
        LOGGER.debug("Server started");
        ChatServer server = new ChatServer(Properties.PORT, "chat.db");
        Executors.newFixedThreadPool(1).execute(server);
        server.addFilter(new JavaOnlyFilter());
        processCommands(server);
        System.exit(0);
    }

    private static void processCommands(ChatServer server) {
        LOGGER.debug("Server is ready to process commands");
        Scanner scanner = new Scanner(System.in);
        String command = "";
        System.out.println("Server started, press: " +
                "\n\"q\" for exit " +
                "\n\"r\" to restart" +
                "\nul for a user list");
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
                    System.out.println("press: " +
                            "\n\"q\" for exit " +
                            "\n\"r\" to restart" +
                            "\nul for a user list");
            }
        }
    }
}