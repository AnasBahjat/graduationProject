package com.example.graduationproject.models;

import android.media.Image;

public class Student {
    private  String name;
    private String email;
    private Image prfileImage;
    private String idNumber;
    private String field;
    private Address address;

    public Student(String name, String email, Image prfileImage, String idNumber, String field, Address address) {
        this.name = name;
        this.email = email;
        this.prfileImage = prfileImage;
        this.idNumber = idNumber;
        this.field = field;
        this.address = address;
    }

    public Student() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
