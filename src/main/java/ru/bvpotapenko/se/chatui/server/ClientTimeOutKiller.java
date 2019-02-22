package ru.bvpotapenko.se.chatui.server;

import java.util.Timer;
import java.util.TimerTask;

public class ClientTimeOutKiller extends TimerTask {
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
            System.out.println("Client " + client.getClientName() + " timeout");
        }
    }
}
