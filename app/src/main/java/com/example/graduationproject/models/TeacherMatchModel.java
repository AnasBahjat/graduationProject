package com.example.graduationproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class TeacherMatchModel implements Parcelable, Serializable {
    private int matchingId ;
    private int childId ;
    private String parentEmail ;
    private CustomChildData customChildData ;
    private String choseDays ;
    private String courses ;
    private String location ;
    private String teachingMethod ;
    private String startTime ;
    private String endTime ;
    private Children children ;
    private Parent parent ;
    private double priceMinimum ;
    private double priceMaximum ;


    public TeacherMatchModel(int matchingId, String parentEmail,
                             CustomChildData customChildData, String choseDays,
                             String courses, String location,
                             String teachingMethod, Children children,
                             String startTime,String endTime) {
        this.matchingId = matchingId;
        this.parentEmail = parentEmail;
        this.customChildData = customChildData;
        this.choseDays = choseDays;
        this.courses = courses;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.children = children;
        this.startTime=startTime;
        this.endTime=endTime;
    }

    public TeacherMatchModel(int matchingId, String parentEmail,
                             CustomChildData customChildData, String choseDays,
                             String courses, String location,
                             String teachingMethod, Children children,
                             String startTime,String endTime,double priceMinimum,double priceMaximum) {
        this.matchingId = matchingId;
        this.parentEmail = parentEmail;
        this.customChildData = customChildData;
        this.choseDays = choseDays;
        this.courses = courses;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.children = children;
        this.startTime=startTime;
        this.endTime=endTime;
        this.priceMinimum=priceMinimum;
        this.priceMaximum=priceMaximum;
    }

    public TeacherMatchModel(CustomChildData customChildData, String choseDays, String courses, String location, String teachingMethod,String startTime,String endTime) {
        this.customChildData = customChildData;
        this.choseDays = choseDays;
        this.courses = courses;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.startTime=startTime;
        this.endTime=endTime;
    }

    public TeacherMatchModel(CustomChildData customChildData, String choseDays, String courses, String location,
                             String teachingMethod,String startTime,String endTime,double priceMinimum,double priceMaximum) {
        this.customChildData = customChildData;
        this.choseDays = choseDays;
        this.courses = courses;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.startTime=startTime;
        this.endTime=endTime;
        this.priceMaximum=priceMaximum;
        this.priceMinimum=priceMinimum;
    }

    public TeacherMatchModel(int matchingId,CustomChildData customChildData, String choseDays, String courses, String location,
                             String teachingMethod,String startTime,String endTime) {
        this.matchingId=matchingId;
        this.customChildData = customChildData;
        this.choseDays = choseDays;
        this.courses = courses;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.startTime=startTime;
        this.endTime=endTime;
    }
    public TeacherMatchModel(int matchingId,CustomChildData customChildData, String choseDays, String courses, String location,
                             String teachingMethod,String startTime,String endTime,double priceMinimum,double priceMaximum) {
        this.matchingId=matchingId;
        this.customChildData = customChildData;
        this.choseDays = choseDays;
        this.courses = courses;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.startTime=startTime;
        this.endTime=endTime;
        this.priceMinimum=priceMinimum;
        this.priceMaximum=priceMaximum;
    }

    public TeacherMatchModel(int matchingId,CustomChildData customChildData,
                             String choseDays, String courses, String location,
                             String teachingMethod,String startTime,
                             String endTime,Parent parent) {
        this.matchingId=matchingId;
        this.customChildData = customChildData;
        this.choseDays = choseDays;
        this.courses = courses;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.startTime=startTime;
        this.endTime=endTime;
        this.parent=parent;

    }
    public TeacherMatchModel(int matchingId,CustomChildData customChildData,
                             String choseDays, String courses, String location,
                             String teachingMethod,String startTime,
                             String endTime,double priceMinimum,double priceMaximum,Parent parent) {
        this.matchingId=matchingId;
        this.customChildData = customChildData;
        this.choseDays = choseDays;
        this.courses = courses;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.startTime=startTime;
        this.endTime=endTime;
        this.parent=parent;
        this.priceMinimum=priceMinimum;
        this.priceMaximum = priceMaximum;

    }



    protected TeacherMatchModel(Parcel in) {
        matchingId = in.readInt();
        parentEmail = in.readString();
        customChildData = in.readParcelable(CustomChildData.class.getClassLoader());
        choseDays = in.readString();
        courses = in.readString();
        location = in.readString();
        teachingMethod = in.readString();
        children = in.readParcelable(Children.class.getClassLoader());
        startTime = in.readString();
        endTime = in.readString();
        parent=in.readParcelable(Parent.class.getClassLoader());
        priceMinimum=in.readDouble();
        priceMaximum=in.readDouble();
    }

    public static final Creator<TeacherMatchModel> CREATOR = new Creator<TeacherMatchModel>() {
        @Override
        public TeacherMatchModel createFromParcel(Parcel in) {
            return new TeacherMatchModel(in);
        }

        @Override
        public TeacherMatchModel[] newArray(int size) {
            return new TeacherMatchModel[size];
        }
    };

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Children getChildren() {
        return children;
    }

    public void setChildren(Children children) {
        this.children = children;
    }

    public int getMatchingId() {
        return matchingId;
    }

    public void setMatchingId(int matchingId) {
        this.matchingId = matchingId;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public CustomChildData getCustomChildData() {
        return customChildData;
    }

    public void setCustomChildData(CustomChildData customChildData) {
        this.customChildData = customChildData;
    }

    public String getChoseDays() {
        return choseDays;
    }

    public void setChoseDays(String choseDays) {
        this.choseDays = choseDays;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
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

    @Override
    public int describeContents() {
        return 0;
    }

    public double getPriceMinimum() {
        return priceMinimum;
    }

    public void setPriceMinimum(double priceMinimum) {
        this.priceMinimum = priceMinimum;
    }

    public double getPriceMaximum() {
        return priceMaximum;
    }

    public void setPriceMaximum(double priceMaximum) {
        this.priceMaximum = priceMaximum;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(matchingId);
        dest.writeString(parentEmail);
        dest.writeParcelable(customChildData, flags);
        dest.writeString(choseDays);
        dest.writeString(courses);
        dest.writeString(location);
        dest.writeString(teachingMethod);
        dest.writeParcelable(children, flags);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeDouble(priceMaximum);
        dest.writeDouble(priceMinimum);
    }
}
