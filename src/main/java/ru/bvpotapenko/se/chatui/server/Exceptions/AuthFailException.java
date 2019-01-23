package ru.bvpotapenko.se.chatui.server.Exceptions;

public class AuthFailException extends Exception{
    private static final String AUTH_ERROR = "Authentication error";
    public String getFailInfo(){
        return AUTH_ERROR;
    }
}
