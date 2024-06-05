package com.example.graduationproject.models;

import android.media.Image;

public class Student {
    private  String name;
    private Image prfileImage;
    private String idNumber;
    private String field;
    private Address address;
    private String birthdate;
    private Profile stdProfile;


    public Student(String name, Image prfileImage, String idNumber, String field, Address address, String birthdate, Profile stdProfile) {
        this.name = name;
        this.prfileImage = prfileImage;
        this.idNumber = idNumber;
        this.field = field;
        this.address = address;
        this.birthdate = birthdate;
        this.stdProfile = stdProfile;
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
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
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
