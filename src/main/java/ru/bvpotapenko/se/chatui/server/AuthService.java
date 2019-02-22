package ru.bvpotapenko.se.chatui.server;

import ru.bvpotapenko.se.chatui.server.Exceptions.AuthFailException;
import ru.bvpotapenko.se.chatui.server.Exceptions.AuthNameDoubled;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        String clientCredentials = "";
        while (client != null && client.isUp() && !client.isAuthorized()) {
            try {
                clientCredentials = client.getSocketReader().readUTF();
            } catch (IOException e) {
                client.sendMessage("Auth error");
            }
            if (isAuthOk(clientCredentials)) {
                try {
                    server.addClient(clientCredentials, client);
                    client.setAuthorized(true);
                    System.out.println("Client authorized :" + clientCredentials);
                } catch (AuthFailException e) {
                    e.printStackTrace();
                }
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
            System.out.println("LOG AuthService tryLogin: " + login + " " + passHash);
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
