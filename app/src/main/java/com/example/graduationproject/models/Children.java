package com.example.graduationproject.models;

public class Children {
    private String childName;
    private String childAge ;
    private int childGender ; // 0 : Female , 1 : Male
    private int grade ;

    public Children(String childName,String childAge,int childGender,int grade){
        this.childName=childName;
        this.childAge=childAge;
        this.childGender=childGender;
        this.grade=grade;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildAge() {
        return childAge;
    }

    public void setChildAge(String childAge) {
        this.childAge = childAge;
    }

    public int getChildGender() {
        return childGender;
    }

    public void setChildGender(int childGender) {
        this.childGender = childGender;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
