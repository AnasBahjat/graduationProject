package com.example.graduationproject.models;

public class Job {
    private int jobId ;
    private String jobTitle;
    private String jobCategory ;


    public Job(int jobId,String jobTitle , String jobCategory){
        this.jobId=jobId;
        this.jobTitle=jobTitle;
        this.jobCategory= jobCategory;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }
}
