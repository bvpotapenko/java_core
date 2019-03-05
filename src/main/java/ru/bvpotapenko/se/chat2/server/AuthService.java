package ru.bvpotapenko.se.chat2.server;

import ru.bvpotapenko.se.chat2.server.Exceptions.AuthFailException;
import ru.bvpotapenko.se.chat2.server.Exceptions.AuthNameDoubled;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthService implements Callable<Boolean> {
    private ChatServer server;
    private ChatServerClient client;
    private Pattern pattern = Pattern.compile("^[/]auth\\s(?<login>\\w+)[&](?<passHash>.+)$");

    public AuthService(ChatServer server, ChatServerClient client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public Boolean call() {
        System.out.println("LOG DEBUG STEP-5: AUTH start");
        String clientCredentials = "";
        if (client != null && client.isUp() && !client.isAuthorized()) {
            try {
                System.out.println("LOG DEBUG STEP-6: AUTH waits for login-pass");
                clientCredentials = getAuthMessage();
                System.out.println("LOG AUTH: clientCredentials: " + clientCredentials);
            } catch (IOException e) {
                client.sendMessage("Auth error");
            }
            System.out.println("LOG DEBUG STEP-7: AUTH checks login-pass");
            if (isAuthOk(clientCredentials)) {
                return true;
            }
        }
        return false;
    }

    // TODO: 05-Mar-19 replace later with JSON-object
    private String getAuthMessage() throws IOException {
        byte[] arbytes = new byte[client.getSocketReader().readInt()];
        for (int i = 0; i < arbytes.length; i++) {
            arbytes[i] = client.getSocketReader().readByte();
        }
        return new String(arbytes);
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
