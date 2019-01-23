package ru.bvpotapenko.se.chatui.server.Exceptions;

public class AuthNameDoubled extends AuthFailException {
    private String login;
   public AuthNameDoubled(String login){
        super();
        this.login = login;
    }
}
