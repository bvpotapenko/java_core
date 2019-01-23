package ru.bvpotapenko.se.chatui.server;

import ru.bvpotapenko.se.chatui.server.Exceptions.AuthFailException;
import ru.bvpotapenko.se.chatui.server.Exceptions.AuthNameDoubled;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthService implements Runnable {
    private ChatServer server;
    private ChatServerClient client;
    private Pattern pattern = Pattern.compile("^[/]auth\\s(?<login>\\w+)[&](?<passHash>.+)$");

    public AuthService(ChatServer server, ChatServerClient client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        String clientCridentials = "";
        while (client != null && client.isUp() && !client.isAuthorized()) {
            try {
                clientCridentials = client.getSocketReader().readUTF();
            } catch (IOException e) {
                client.sendMessage("Auth error");
            }
            if (isAuthOk(clientCridentials)) {
                server.addClient(clientCridentials, client);
                client.setAuthorized(true);
                System.out.println("Client authorized :" + clientCridentials);
            } else
                server.removeClient(client);
        }
    }

    private boolean isAuthOk(String clientCredentials) {
        Matcher matcher = pattern.matcher(clientCredentials);
        if (matcher.find()) {
            String login = matcher.group("login");
            String passHash = matcher.group("passHash");
            try {
                tryLogin(login, passHash);
            } catch (AuthNameDoubled dne) {
                return getErrorMessage(dne, "Login is already in use.");
            } catch (AuthFailException afe) {
                return getErrorMessage(afe, "Login or pass is wrong.");
            }
            return true;
        }
        return false;
    }

    private void tryLogin(String login, String passHash) throws AuthFailException {
        boolean authSuccess = false;
        if (server.getUserList().contains(login))
            throw new AuthNameDoubled(login);
        try {
            authSuccess = SQLHandler.auth(login, passHash);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (authSuccess)
            return;

        throw new AuthFailException();
    }

    private boolean getErrorMessage(AuthFailException e, String errorMessage) {
        System.out.println(e.getFailInfo());
        client.sendMessage(errorMessage);
        return false;
    }
}
