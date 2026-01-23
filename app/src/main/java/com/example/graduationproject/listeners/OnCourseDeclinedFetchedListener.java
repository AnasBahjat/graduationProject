package com.example.graduationproject.listeners;

import com.example.graduationproject.models.Course;

import org.json.JSONArray;

public interface OnCourseDeclinedFetchedListener {
    void onCourseDeclined(int flag, JSONArray courseDeclined);
}
