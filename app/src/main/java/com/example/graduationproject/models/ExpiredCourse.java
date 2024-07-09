package com.example.graduationproject.models;

public class ExpiredCourse {
    private int courseId;
    private int teacherSentRequestId;
    private int parentSentRequestId;

    public ExpiredCourse(int courseId, int teacherSentRequestId, int parentSentRequestId) {
        this.courseId = courseId;
        this.teacherSentRequestId = teacherSentRequestId;
        this.parentSentRequestId = parentSentRequestId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getTeacherSentRequestId() {
        return teacherSentRequestId;
    }

    public void setTeacherSentRequestId(int teacherSentRequestId) {
        this.teacherSentRequestId = teacherSentRequestId;
    }

    public int getParentSentRequestId() {
        return parentSentRequestId;
    }

    public void setParentSentRequestId(int parentSentRequestId) {
        this.parentSentRequestId = parentSentRequestId;
    }
}
