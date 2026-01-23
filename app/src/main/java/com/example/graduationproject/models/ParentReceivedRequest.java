package com.example.graduationproject.models;

public class ParentReceivedRequest {
    private  int requestId ;
    private TeacherMatchModel teacherMatchModel;
    private Teacher teacher ;
    private int isAccepted ;
    private String requestDate;
    private String requestTime;

    public ParentReceivedRequest(int requestId, TeacherMatchModel teacherMatchModel, Teacher teacher, int isAccepted, String requestDate, String requestTime) {
        this.requestId = requestId;
        this.teacherMatchModel = teacherMatchModel;
        this.teacher = teacher;
        this.isAccepted = isAccepted;
        this.requestDate = requestDate;
        this.requestTime = requestTime;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public TeacherMatchModel getTeacherMatchModel() {
        return teacherMatchModel;
    }

    public void setTeacherMatchModel(TeacherMatchModel teacherMatchModel) {
        this.teacherMatchModel = teacherMatchModel;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public int getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(int isAccepted) {
        this.isAccepted = isAccepted;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }
}
