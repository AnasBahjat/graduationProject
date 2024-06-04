package com.example.graduationproject.models;

import android.media.Image;

public class Student extends Parent{
    private  String name;
    private Image prfileImage;
    private String idNumber;
    private String field;
    private Address address;
    private String birthdate;


    public Student() {

    }

    public Student(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Image getPrfileImage() {
        return prfileImage;
    }

    public void setPrfileImage(Image prfileImage) {
        this.prfileImage = prfileImage;
    }

    @Override
    public String getIdNumber() {
        return idNumber;
    }

    @Override
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public void setField(String field) {
        this.field = field;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
