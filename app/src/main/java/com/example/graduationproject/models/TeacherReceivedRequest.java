package com.example.graduationproject.models;

import java.util.List;

public class TeacherReceivedRequest {
    private int parentRequestId ;
    private TeacherPostRequest teacherPostRequest;
    private Parent parent ;
    private List<Children> children ;
    private int isAccepted ;
    private String requestDate;
    private String requestTime;

    public TeacherReceivedRequest(int parentRequestId, TeacherPostRequest teacherPostRequest,
                                  Parent parent, List<Children> children,int isAccepted,
                                  String requestDate, String requestTime) {
        this.parentRequestId = parentRequestId;
        this.teacherPostRequest = teacherPostRequest;
        this.parent = parent;
        this.children = children;
        this.isAccepted = isAccepted;
        this.requestDate = requestDate;
        this.requestTime = requestTime;
    }


    public int getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(int isAccepted) {
        this.isAccepted = isAccepted;
    }

    public int getParentRequestId() {
        return parentRequestId;
    }

    public void setParentRequestId(int parentRequestId) {
        this.parentRequestId = parentRequestId;
    }

    public TeacherPostRequest getTeacherPostRequest() {
        return teacherPostRequest;
    }

    public void setTeacherPostRequest(TeacherPostRequest teacherPostRequest) {
        this.teacherPostRequest = teacherPostRequest;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public List<Children> getChildren() {
        return children;
    }

    public void setChildren(List<Children> children) {
        this.children = children;
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
