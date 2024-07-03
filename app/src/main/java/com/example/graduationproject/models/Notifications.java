package com.example.graduationproject.models;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Notifications implements Parcelable {
    private int notificationId ;
    private int notificationType ; // 0 : Confirm account ..
    private String notificationTitle;
    private String notificationBody;
    private int isNotificationRead;
    public Notifications(int notificationType , String notificationTitle,String notificationBody,int isNotificationRead){
        this.notificationType = notificationType;
        this.notificationBody=notificationBody;
        this.notificationTitle=notificationTitle;
        this.isNotificationRead=isNotificationRead;
    }

    public Notifications(int notificationId, int notificationType, String notificationTitle, String notificationBody, int isNotificationRead) {
        this.notificationId = notificationId;
        this.notificationType = notificationType;
        this.notificationTitle = notificationTitle;
        this.notificationBody = notificationBody;
        this.isNotificationRead = isNotificationRead;
    }

    protected Notifications(Parcel in) {
        notificationId = in.readInt();
        notificationType = in.readInt();
        notificationTitle = in.readString();
        notificationBody = in.readString();
        isNotificationRead = in.readInt();
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public static final Creator<Notifications> CREATOR = new Creator<Notifications>() {
        @Override
        public Notifications createFromParcel(Parcel in) {
            return new Notifications(in);
        }

        @Override
        public Notifications[] newArray(int size) {
            return new Notifications[size];
        }
    };

    public int getIsNotificationRead() {
        return isNotificationRead;
    }

    public void setIsNotificationRead(int isNotificationRead) {
        this.isNotificationRead = isNotificationRead;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(notificationType);
        dest.writeString(notificationTitle);
        dest.writeString(notificationBody);
        dest.writeInt(isNotificationRead);
        dest.writeInt(notificationId);
    }

    @NonNull
    @Override
    public String toString() {
        return "Notifications{notificationType='"+notificationType+"', notificationTitle="+notificationTitle+"', notificationBody="+notificationBody+"', isNotificationRead="+isNotificationRead+"}";
    }
}
