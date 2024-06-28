package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface OnProfileDataFetchListener {
    void onProfileDataFetched(int flag, JSONArray profileData);
}
