package com.example.graduationproject.models;

import java.util.List;

public class ParentRequestToSend {
    private int teacherPostRequestId ;
    private String parentEmail ;
    private int childId ;
    private List<Integer> childrenIds ;

    public ParentRequestToSend(int teacherPostRequestId,String parentEmail , int child){
        this.teacherPostRequestId = teacherPostRequestId;
        this.parentEmail = parentEmail;
        this.childId = child;
    }

    public ParentRequestToSend(int teacherPostRequestId,String parentEmail,List<Integer> children){
        this.teacherPostRequestId = teacherPostRequestId;
        this.parentEmail = parentEmail;
        this.childrenIds = children;
    }

    public int getTeacherPostRequestId() {
        return teacherPostRequestId;
    }

    public void setTeacherPostRequestId(int teacherPostRequestId) {
        this.teacherPostRequestId = teacherPostRequestId;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public List<Integer> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<Integer> children) {
        this.childrenIds = children;
    }
}
