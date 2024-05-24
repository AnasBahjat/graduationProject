package com.example.graduationproject.models;

public class Notifications {
    private int notificationType ; // 0 : Confirm account ..
    private String notificationTitle;
    private String notificationBody;
    public Notifications(int notificationType , String notificationTitle,String notificationBody){
        this.notificationType = notificationType;
        this.notificationBody=notificationBody;
        this.notificationTitle=notificationTitle;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
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
