package ru.bvpotapenko.se.chatui.server;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;

    public static void connect(String dbName) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlLite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    public static boolean auth(String login, String passHash) throws SQLException {
        boolean isAuthOk = false;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(
                "SELECT count(nick) FROM \"users_table\" WHERE login = \"" + login +
                        "\" AND password = \"" + passHash + "\"");

        if(rs.next() && rs.getInt("count(nick)") > 0){
           isAuthOk = true;
        }
        return isAuthOk;
    }
}
