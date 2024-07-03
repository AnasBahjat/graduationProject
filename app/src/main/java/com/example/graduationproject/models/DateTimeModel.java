package com.example.graduationproject.models;

public class DateTimeModel {
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String days ;

    public DateTimeModel(String startDate,String endDate,String startTime,String endTime,String days) {
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.days=days;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
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
}
