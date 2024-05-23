package com.example.graduationproject.models;

public class Notifications {
    private String notificationTitle;
    private String notificationBody;
    public Notifications(String notificationTitle,String notificationBody){
        this.notificationBody=notificationBody;
        this.notificationTitle=notificationTitle;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }
}
