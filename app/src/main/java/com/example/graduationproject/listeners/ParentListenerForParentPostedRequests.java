package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface ParentListenerForParentPostedRequests {
    void onPostedParentRequests(int resultFlag , JSONArray parentInformation);
}
