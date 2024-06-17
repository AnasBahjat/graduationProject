package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface AddTeacherMatchingListener {
    void onMatchingAdded(int resultFlag);
    void getTeacherMatchingData(int resultFlag, JSONArray teacherMatchingData);
}
