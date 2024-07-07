package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface OnParentCoursesFetchedForConflictListener {
    void onParentCoursesFetched(int flag, JSONArray coursesDatesTeacherTable,JSONArray coursesDatesParentTable);
}
