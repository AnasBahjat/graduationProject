package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface OnTeacherCoursesFetchedListener {
    void onTeacherCoursesFetched(int flag, JSONArray teacherCourses);
}
