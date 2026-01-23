package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface OnReceivedRequestsListener {
    void onRequestsReceived(int flag, JSONArray requestsData);
}
