package com.example.graduationproject.listeners;

import org.json.JSONArray;
import org.json.JSONException;

public interface OnAllTeacherPostedRequestsForParentListener {
    void onAllTeacherPostedRequestsForParentFetched(int flag, JSONArray teacherPostsData) throws JSONException;
}
