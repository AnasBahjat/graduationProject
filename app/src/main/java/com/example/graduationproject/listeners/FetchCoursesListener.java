package com.example.graduationproject.listeners;

import org.json.JSONArray;

import java.util.ArrayList;

public interface FetchCoursesListener {
    void onCoursesFetched(int flag , JSONArray courses);
}
