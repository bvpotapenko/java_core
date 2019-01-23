package ru.bvpotapenko.se.chatui.server.filters;

import java.util.LinkedList;
import java.util.List;

public class JavaOnlyFilter implements ChatFIlter {
    List<String> censuredList;

    public JavaOnlyFilter(){
        censuredList = new LinkedList<>();
        censuredList.add("tea");
        censuredList.add("water");
        censuredList.add("wine");
    }
    @Override
    public String filter(String message) {
        for(String word: censuredList){
            message = message.replaceAll(word, "JAVA");
        }
        return message;
    }
}
