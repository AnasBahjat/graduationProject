package com.example.graduationproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Children implements Parcelable {
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

    protected Children(Parcel in) {
        childName = in.readString();
        childAge = in.readString();
        childGender = in.readInt();
        grade = in.readInt();
    }

    public static final Creator<Children> CREATOR = new Creator<Children>() {
        @Override
        public Children createFromParcel(Parcel in) {
            return new Children(in);
        }

        @Override
        public Children[] newArray(int size) {
            return new Children[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(childName);
        dest.writeString(childAge);
        dest.writeInt(childGender);
        dest.writeInt(grade);
    }
}
