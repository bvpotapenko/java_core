package ru.bvpotapenko.se.chat2.model;

import java.time.*;
import java.util.UUID;

public class ChatMessage extends AMessage {
    public String id = UUID.randomUUID().toString();
    private ZonedDateTime zonedDateTime = ZonedDateTime.now();
    String receiverID;
    String replyToMessageID;
    MessageContent messageCointent;


    public void test(){
        System.out.println(zonedDateTime.getOffset().toString());
        System.out.println(zonedDateTime.toString());
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        LocalDateTime.now();
        zonedDateTime = zonedDateTime.withZoneSameInstant(zoneId);
        System.out.println(zonedDateTime);
        System.out.println(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()));
    }
}
