package com.example.graduationproject.listeners;

import com.example.graduationproject.models.Notifications;

import org.json.JSONArray;

import java.util.List;

public interface NotificationsListListener {
   void getNotifications(int result , JSONArray notificationsJsonArray);
}
