package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface OnTeacherReceivedRequestsListener {
    void onTeacherRequestsReceived(int flag, JSONArray requestsData);
}
