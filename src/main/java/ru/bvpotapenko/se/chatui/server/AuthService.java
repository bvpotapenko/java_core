package ru.bvpotapenko.se.chatui.server;

import java.util.Map;

public class AuthService implements Runnable {
    private Map<String, ChatServerClient> clients;
    private ChatServerClient client;

    public AuthService(Map<String, ChatServerClient> clients, ChatServerClient client) {
        this.clients = clients;
        this.client = client;
    }

    @Override
    public void run() {
        String clientName = "";
        while (!client.isAuthorized()) {
            clientName = client.getClientName();
            if (clientName == null || clientName.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.err.println("Client get name error: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                if (clients.containsKey(clientName)) {
                    clients.get(clientName).stop();
                }
                client.setAuthorized(true);
                System.out.println("Client authorized :"+ clientName );
            }
        }
        clients.put(clientName, client);
    }
}
