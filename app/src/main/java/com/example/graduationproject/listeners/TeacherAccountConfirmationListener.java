package com.example.graduationproject.listeners;

public interface TeacherAccountConfirmationListener {
    void onResult(int resultFlag);
    void updateTeacherFragment(int resultFlag,int doneFlag);
}
