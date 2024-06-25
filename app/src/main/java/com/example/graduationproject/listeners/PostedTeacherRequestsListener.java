package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface PostedTeacherRequestsListener {
    void onDataFetched(int flag, JSONArray data);
}
