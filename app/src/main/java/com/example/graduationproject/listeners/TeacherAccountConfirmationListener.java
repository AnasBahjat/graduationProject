package com.example.graduationproject.listeners;

public interface TeacherAccountConfirmationListener {
    void onResult(int resultFlag);
    void updateTeacherNotifications(int resultFlag,int notificationId);
    void confirmNotificationDeleted(int flag);
}
