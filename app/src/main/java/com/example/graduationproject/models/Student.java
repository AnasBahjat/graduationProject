package com.example.graduationproject.models;

import android.media.Image;

public class Student {
    private  String name;
    private Image prfileImage;
    private String parentIdNumber;

    private  String parentEmail;
    private String field;
    private Address address;
    private String birthdate;
    private Profile stdProfile;
    private String eduLevel;


    public Student(String name, Image prfileImage, String parentIdNumber, String field, Address address, String birthdate, Profile stdProfile) {
        this.name = name;
        this.prfileImage = prfileImage;
        this.parentIdNumber = parentIdNumber;
        this.field = field;
        this.address = address;
        this.birthdate = birthdate;
        this.stdProfile = stdProfile;
    }

    public Student(String name, String parentIdNumber, String parentEmail) {
        this.name = name;
        this.parentIdNumber = parentIdNumber;
        this.parentEmail = parentEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getPrfileImage() {
        return prfileImage;
    }

    public void setPrfileImage(Image prfileImage) {
        this.prfileImage = prfileImage;
    }

    public String getIdNumber() {
        return parentIdNumber;
    }

    public void setIdNumber(String parentIdNumber) {
        this.parentIdNumber = parentIdNumber;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Profile getStdProfile() {
        return stdProfile;
    }

    public void setStdProfile(Profile stdProfile) {
        this.stdProfile = stdProfile;
    }
}
