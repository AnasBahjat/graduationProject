package com.example.graduationproject.listeners;

import com.example.graduationproject.models.TeacherReceivedRequest;

public interface OnAcceptDeclineTeacherRequestsListener {
    void onAcceptDeclineClicked(int flag, TeacherReceivedRequest teacherReceivedRequest);
}
