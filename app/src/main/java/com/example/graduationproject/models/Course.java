package com.example.graduationproject.models;

import com.example.graduationproject.ui.teacherUi.TeacherFragment;

public class Course {
    private int courseId;
    private String teacherEmail ;
    private String parentEmail ;
    private int parentSentRequestId ;
    private int teacherSentRequestId ;
    private int childId;
    private String courses ;
    private int duration ;
    private String days ;
    private String location;
    private String educationLevel;
    private String teachingMethod;
    private String startTime;
    private String endTime ;
    private String startDate ;
    private String endDate ;
    private double price ;
    private Children child;
    private Parent parent;
    private Teacher teacher;

    public Course(int courseId, String teacherEmail,
                  String parentEmail, int parentSentRequestId,
                  int teacherSentRequestId, int childId, String courses,
                  int duration, String days, String location,
                  String teachingMethod, String startTime, String endTime,
                  String startDate, String endDate, double price,
                  Children child, Parent parent, Teacher teacher) {
        this.courseId = courseId;
        this.teacherEmail = teacherEmail;
        this.parentEmail = parentEmail;
        this.parentSentRequestId = parentSentRequestId;
        this.teacherSentRequestId = teacherSentRequestId;
        this.childId = childId;
        this.courses = courses;
        this.duration = duration;
        this.days = days;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.child = child;
        this.parent = parent;
        this.teacher = teacher;
    }

    public int getParentSentRequestId() {
        return parentSentRequestId;
    }

    public void setParentSentRequestId(int parentSentRequestId) {
        this.parentSentRequestId = parentSentRequestId;
    }

    public int getTeacherSentRequestId() {
        return teacherSentRequestId;
    }

    public void setTeacherSentRequestId(int teacherSentRequestId) {
        this.teacherSentRequestId = teacherSentRequestId;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Children getChild() {
        return child;
    }

    public void setChild(Children child) {
        this.child = child;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public int getTeacherRequestId() {
        return teacherSentRequestId;
    }

    public void setTeacherRequestId(int teacherRequestId) {
        this.teacherSentRequestId = teacherRequestId;
    }

    public int getParentRequestId() {
        return parentSentRequestId;
    }

    public void setParentRequestId(int parentRequestId) {
        this.parentSentRequestId = parentRequestId;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTeachingMethod() {
        return teachingMethod;
    }

    public void setTeachingMethod(String teachingMethod) {
        this.teachingMethod = teachingMethod;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
