package com.example.graduationproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TeacherPostRequest implements Parcelable {
    private int teacherPostRequestId ;
    private String teacherEmail;
    private String courses ;
    private String educationLevel ;
    private String duration ;
    private String availability;
    private String location ;
    private String teachingMethod ;
    private Teacher teacherData ;
    private String startTime ;
    private String endTime ;


    public TeacherPostRequest(int teacherPostRequestId,String teacherEmail,
                              String courses, String educationLevel,
                              String duration, String location, String teachingMethod,
                              String startTime,String endTime) {
        this.teacherPostRequestId = teacherPostRequestId;
        this.teacherEmail = teacherEmail;
        this.courses = courses;
        this.educationLevel = educationLevel;
        this.duration = duration;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.startTime=startTime;
        this.endTime=endTime;
    }

    public TeacherPostRequest(int teacherPostRequestId,String teacherEmail,
                              String courses, String educationLevel,
                              String duration,String availability, String location, String teachingMethod,
                              String startTime,String endTime) {
        this.teacherPostRequestId = teacherPostRequestId;
        this.teacherEmail = teacherEmail;
        this.courses = courses;
        this.educationLevel = educationLevel;
        this.duration = duration;
        this.availability = availability;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.startTime=startTime;
        this.endTime=endTime;
    }





    public TeacherPostRequest(int teacherPostRequestId, String teacherEmail,
                              String courses, String educationLevel,
                              String duration,String availability, String location,
                              String teachingMethod, Teacher teacherData,String startTime,String endTime) {
        this.teacherPostRequestId = teacherPostRequestId;
        this.teacherEmail = teacherEmail;
        this.courses = courses;
        this.educationLevel = educationLevel;
        this.duration = duration;
        this.availability=availability;
        this.location = location;
        this.teachingMethod = teachingMethod;
        this.teacherData = teacherData;
        this.startTime=startTime;
        this.endTime=endTime;
    }

    protected TeacherPostRequest(Parcel in) {
        teacherPostRequestId = in.readInt();
        teacherEmail = in.readString();
        courses = in.readString();
        educationLevel = in.readString();
        duration = in.readString();
        availability=in.readString();
        location = in.readString();
        teachingMethod = in.readString();
        teacherData = in.readParcelable(Teacher.class.getClassLoader());
        startTime = in.readString();
        endTime = in.readString();
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public static final Creator<TeacherPostRequest> CREATOR = new Creator<TeacherPostRequest>() {
        @Override
        public TeacherPostRequest createFromParcel(Parcel in) {
            return new TeacherPostRequest(in);
        }

        @Override
        public TeacherPostRequest[] newArray(int size) {
            return new TeacherPostRequest[size];
        }
    };

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

    public Teacher getTeacherData() {
        return teacherData;
    }

    public void setTeacherData(Teacher teacherData) {
        this.teacherData = teacherData;
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

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(teacherPostRequestId);
        dest.writeString(teacherEmail);
        dest.writeString(courses);
        dest.writeString(educationLevel);
        dest.writeString(duration);
        dest.writeString(availability);
        dest.writeString(location);
        dest.writeString(teachingMethod);
        dest.writeParcelable(teacherData,0);
        dest.writeString(startTime);
        dest.writeString(endTime);
    }
}
