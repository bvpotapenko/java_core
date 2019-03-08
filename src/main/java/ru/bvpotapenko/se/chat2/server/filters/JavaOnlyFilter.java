package ru.bvpotapenko.se.chat2.server.filters;

import java.util.LinkedList;
import java.util.List;

public class JavaOnlyFilter implements ChatFilter {
    List<String> censuredList;

    public JavaOnlyFilter(){
        censuredList = new LinkedList<>();
        censuredList.add("shit");
        censuredList.add("shit");
        censuredList.add("shit");
    }
    @Override
    public String filter(String message) {
        if(message == null) return "";
        for(String word: censuredList){
            message = message.replaceAll(word, "JAVA");
        }
        return message;
    }
}
