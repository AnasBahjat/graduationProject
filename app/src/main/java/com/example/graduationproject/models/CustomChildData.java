package com.example.graduationproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CustomChildData implements Parcelable {
    private int childId;
    private String childName ;
    private int childGrade ;

    public CustomChildData(int childId, String childName, int childGrade) {
        this.childId = childId;
        this.childName = childName;
        this.childGrade = childGrade;
    }

    protected CustomChildData(Parcel in) {
        childId = in.readInt();
        childName = in.readString();
        childGrade = in.readInt();
    }

    public static final Creator<CustomChildData> CREATOR = new Creator<CustomChildData>() {
        @Override
        public CustomChildData createFromParcel(Parcel in) {
            return new CustomChildData(in);
        }

        @Override
        public CustomChildData[] newArray(int size) {
            return new CustomChildData[size];
        }
    };

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public int getChildGrade() {
        return childGrade;
    }

    public void setChildGrade(int childGrade) {
        this.childGrade = childGrade;
    }

    @NonNull
    @Override
    public String toString() {
        return childName + ", " + childGrade;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(childId);
        dest.writeString(childName);
        dest.writeInt(childGrade);
    }
}
