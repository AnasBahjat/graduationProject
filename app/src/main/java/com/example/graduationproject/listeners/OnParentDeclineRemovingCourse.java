package com.example.graduationproject.listeners;

import com.example.graduationproject.models.Course;

public interface OnParentDeclineRemovingCourse {
    void onDeclineClicked(int flag, Course courseTemp);
}
