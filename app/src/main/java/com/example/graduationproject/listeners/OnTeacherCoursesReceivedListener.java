package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface OnTeacherCoursesReceivedListener {
    void onCoursesReceived(int flag, JSONArray coursesInformation);
}
