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
                "SELECT count(nick) FROM \"users\" WHERE login = ? AND password = ?");
        statement.setString(1, login);
        statement.setString(2, passHash);
        ResultSet rs = statement.executeQuery();

        if(rs.next() && rs.getInt("count(nick)") > 0){
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
        if(rs.next() && !"".equalsIgnoreCase(rs.getString("nickname"))){
            return rs.getString("nickname");
        }
        return "NickNotSet_"+ login.hashCode();
    }
    static void setClientNick(String login, String newNick)throws SQLException{
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE nickname FROM \"users\" WHERE login = ?");
        statement.setString(1, login);
        statement.executeQuery();
    }

    public static void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
