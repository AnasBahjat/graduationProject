package com.example.graduationproject.listeners;

import org.json.JSONArray;

public interface TellParentDataIsReady {
    void onDataReady(int resultFlag, JSONArray teacherData);
}
