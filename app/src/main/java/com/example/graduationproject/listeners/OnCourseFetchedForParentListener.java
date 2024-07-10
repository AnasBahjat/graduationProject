package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface OnCourseFetchedForParentListener {
    void onCourseFetched(int flag, JSONArray courseInformation);
}
