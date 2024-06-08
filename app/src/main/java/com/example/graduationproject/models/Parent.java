package com.example.graduationproject.models;

import java.util.List;

public class Parent {
    private String email ;
    private String idNumber ;
    private String phoneNumber ;
    private List<Children> childrenList;
    private String city;
    private String country ;

    public Parent(String email,String idNumber, String phoneNumber, List<Children> childrenList, String city, String country) {
        this.email=email;
        this.idNumber = idNumber;
        this.phoneNumber = phoneNumber;
        this.childrenList = childrenList;
        this.city = city;
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Children> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<Children> childrenList) {
        this.childrenList = childrenList;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
