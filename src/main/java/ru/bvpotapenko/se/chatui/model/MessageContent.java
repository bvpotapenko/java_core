package ru.bvpotapenko.se.chatui.model;

import java.net.URL;

class MessageContent {
    String text;
    URL fileURL;
    MessageFileTrype messageFileTrype;
    boolean mustBeDownloaded;
}
