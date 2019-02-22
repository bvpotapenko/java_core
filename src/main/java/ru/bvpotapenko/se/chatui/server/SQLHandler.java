package ru.bvpotapenko.se.chatui.server;

import java.sql.*;
import java.util.Random;

/**
 * Default users:
 * u1 p1
 * u2 p2
 */
public class SQLHandler {
    private static Connection connection;

    public static void connect(String dbName) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    public static boolean auth(String login, String passHash) throws SQLException {
        boolean isAuthOk = false;
        PreparedStatement statement = connection.prepareStatement(
                "SELECT password FROM \"users\" WHERE login = ?");
        statement.setString(1, login);
        System.out.println("LOG DEBUG STEP-11: SQLHandler checks password ");
        ResultSet rs = statement.executeQuery();

        if(rs.next() && rs.getString("password").equals(passHash)){
           isAuthOk = true;
        }
        System.out.println("LOG SQLHandler.auth: " + isAuthOk);
        return isAuthOk;
    }

    static String getClientNick(String login)throws SQLException{
        PreparedStatement statement = connection.prepareStatement(
                "SELECT nickname FROM \"users\" WHERE login = ?");
        statement.setString(1, login);
        ResultSet rs = statement.executeQuery();
        if(rs.next() && !rs.getString("nickname").isEmpty()){
            return rs.getString("nickname");
        }
        return "NickNotSet_"+ login.hashCode();
    }
    static void setClientNick(String login, String newNick)throws SQLException{
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE users SET nickname =? WHERE login = ?");
        statement.setString(1, newNick);
        statement.setString(2, login);
        statement.executeUpdate();
    }

    public static void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
