package ru.bvpotapenko.se.chatui.server;

import ru.bvpotapenko.se.chatui.server.Exceptions.AuthFailException;
import ru.bvpotapenko.se.chatui.server.Exceptions.AuthNameDoubled;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
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
        System.out.println("LOG DEBUG STEP-5: AUTH start");
        String clientCredentials = "";
        System.out.println("client != null " + (client != null) +
                "\nclient.isUp() " + client.isUp() +
                "\n!client.isAuthorized()" + !client.isAuthorized());
        while (client != null && client.isUp() && !client.isAuthorized()) {
            try {
                System.out.println("LOG DEBUG STEP-6: AUTH waits for login-pass");
               // clientCredentials = client.getSocketReader().readUTF();
                byte[] arbytes = new byte[client.getSocketReader().readInt()];
                for(int i = 0; i < arbytes.length; i++){
                    arbytes[i] = client.getSocketReader().readByte();
                }
                clientCredentials = new String(arbytes);
                System.out.println("LOG AUTH: clientCredentials: " + clientCredentials);
            } catch (IOException e) {
                client.sendMessage("Auth error");
            }
            System.out.println("LOG DEBUG STEP-7: AUTH checks login-pass");
            if (isAuthOk(clientCredentials)) {
                try {
                    System.out.println("LOG DEBUG STEP-12.authOK!: AUTH tries to add a client");
                    server.addClient(client);
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
        System.out.println("LOG DEBUG STEP-8: AUTH parsing login");
        Matcher matcher = pattern.matcher(clientCredentials);
        if (matcher.find()) {
            String login = matcher.group("login");
            String passHash = matcher.group("passHash");
            client.setClientName(login);
            System.out.println("LOG DEBUG STEP-9: AUTH parsed login-pass");
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
            System.out.println("LOG DEBUG STEP-10: AUTH tryLogin: " + login + " " + passHash);
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
