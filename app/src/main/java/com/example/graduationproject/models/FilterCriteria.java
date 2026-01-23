package com.example.graduationproject.models;

import java.util.List;

public class FilterCriteria {
    private String location;
    private String course;
    private List<String> locationList;
    private List<String> coursesList;
    private List<String> genderList;
    private List<String> gradeList;
    private List<String> teachingMethodList;
    private Double minPrice;
    private Double maxPrice;


    public FilterCriteria(){

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public List<String> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<String> locationList) {
        this.locationList = locationList;
    }

    public List<String> getCoursesList() {
        return coursesList;
    }

    public void setCoursesList(List<String> coursesList) {
        this.coursesList = coursesList;
    }

    public List<String> getGenderList() {
        return genderList;
    }

    public void setGenderList(List<String> genderList) {
        this.genderList = genderList;
    }

    public List<String> getGradeList() {
        return gradeList;
    }

    public void setGradeList(List<String> gradeList) {
        this.gradeList = gradeList;
    }

    public List<String> getTeachingMethodList() {
        return teachingMethodList;
    }

    public void setTeachingMethodList(List<String> teachingMethodList) {
        this.teachingMethodList = teachingMethodList;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
}
