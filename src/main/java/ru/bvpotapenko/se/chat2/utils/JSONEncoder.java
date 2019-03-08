package ru.bvpotapenko.se.chat2.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class JSONEncoder {
    public static <T> T fromJson(String json, Class<T> classOfT){
        Gson gson = new GsonBuilder().create();
        try{
            return gson.fromJson(json, classOfT);
        }catch(JsonSyntaxException jse){
            return null;
        }
    }
    public static String toJson(Object obj){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try{
            return gson.toJson(obj);
        }catch(JsonSyntaxException jse){
            return null;
        }
    }
}
