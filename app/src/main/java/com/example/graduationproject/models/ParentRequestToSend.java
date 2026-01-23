package com.example.graduationproject.models;

import java.util.List;

public class ParentRequestToSend {
    private int teacherPostRequestId ;
    private String parentEmail ;
    private String teacherEmail ;
    private List<Integer> childrenIds ;



    public ParentRequestToSend(int teacherPostRequestId,String parentEmail,String teacherEmail,List<Integer> children){
        this.teacherPostRequestId = teacherPostRequestId;
        this.parentEmail = parentEmail;
        this.teacherEmail = teacherEmail;
        this.childrenIds = children;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
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



    public List<Integer> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<Integer> children) {
        this.childrenIds = children;
    }
}
