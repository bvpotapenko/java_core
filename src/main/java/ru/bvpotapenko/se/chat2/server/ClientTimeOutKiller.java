package ru.bvpotapenko.se.chat2.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class ClientTimeOutKiller extends TimerTask {
    private static final Logger LOGGER = LogManager.getLogger(ClientTimeOutKiller.class);
    private final ChatServerClient client;
    private final ChatServer server;

    ClientTimeOutKiller(ChatServer server, ChatServerClient client) {
        this.server = server;
        this.client = client;
        Timer timer = new Timer();
        timer.schedule(this, 30000);
    }

    @Override
    public void run() {
        if (!client.isAuthorized()) {
            server.removeClient(client);
            LOGGER.debug("Client " + client.getClientName() + " timeout");
        }
    }
}
