package ru.bvpotapenko.se.chat2.server.Exceptions;

public class AuthNameDoubled extends AuthFailException {
    private String login;
   public AuthNameDoubled(String login){
        super();
        this.login = login;
    }
}
