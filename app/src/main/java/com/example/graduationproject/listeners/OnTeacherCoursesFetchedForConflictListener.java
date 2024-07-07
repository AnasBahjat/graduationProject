package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface OnTeacherCoursesFetchedForConflictListener {
    void onTeacherCoursesFetched(int flag, JSONArray coursesDatesTeacherTable,JSONArray coursesDateParentTable);
}
