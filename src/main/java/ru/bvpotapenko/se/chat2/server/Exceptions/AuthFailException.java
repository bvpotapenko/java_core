package ru.bvpotapenko.se.chat2.server.Exceptions;

public class AuthFailException extends Exception{
    private static final String AUTH_ERROR = "Authentication error";
    public String getFailInfo(){
        return AUTH_ERROR;
    }
}
