package ru.bvpotapenko.se.chat2.model;

import java.net.URL;

class MessageContent {
    String text;
    URL fileURL;
    MessageFileType messageFileTrype;
    boolean mustBeDownloaded;
}
