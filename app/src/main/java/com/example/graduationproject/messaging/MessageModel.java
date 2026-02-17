package com.example.graduationproject.messaging;

public class MessageModel {
    private String message;
    private boolean read;
    private String receiverId;
    private String senderId;
    private long timestamp;


    public MessageModel() {}


    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public String getReceiverId() { return receiverId; }
    public String getSenderId() { return senderId; }
    public long getTimestamp() { return timestamp; }
}
