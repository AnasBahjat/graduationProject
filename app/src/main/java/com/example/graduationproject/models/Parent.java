package com.example.graduationproject.models;

import android.media.Image;

import java.util.List;

public class Parent {
    private  String name;
    private String email;
    private String idNumber;
    private String field;
    private Address address;
    private  List<Student>  stdlist;


    public Parent(String name, String email, String idNumber, String field, Address address, List stdlist) {
        this.name = name;
        this.email = email;
        this.idNumber = idNumber;
        this.field = field;
        this.address = address;
        this.stdlist = stdlist;
    }



    public Parent() {
        super();

    }

    public List getStdlist() {
        return stdlist;
    }

    public void setStdlist(List stdlist) {
        this.stdlist = stdlist;
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


    public  void addstd(Student std){
        this.stdlist.add(std);
    }
}
