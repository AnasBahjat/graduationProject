package com.example.graduationproject.models;

public class Teacher {
    private String email;
    private String idNumber;
    private String studentOrGraduate;
    private String expectedGraduationYear;
    private String college;
    private String field;

    private String availability ;
    private String educationalLevel ;
    private Address address;
    private String phoneNumber ;
    public Teacher(String email,String idNumber,
                   String studentOrGraduate,String expectedGraduationYear,
                   String college,String field,
                   String availability,
                   String educationalLevel ,
                   Address address){

        this.email=email;
        this.idNumber=idNumber;
        this.studentOrGraduate=studentOrGraduate;
        this.expectedGraduationYear=expectedGraduationYear;
        this.college=college;
        this.field=field;
        this.availability=availability;
        this.educationalLevel = educationalLevel;
        this.address=address;
    }

    public Teacher(String email,String idNumber,
                   String studentOrGraduate,String expectedGraduationYear,
                   String college,String field,
                   String availability,
                   String educationalLevel ,
                   Address address,
                   String phoneNumber){

        this.email=email;
        this.idNumber=idNumber;
        this.studentOrGraduate=studentOrGraduate;
        this.expectedGraduationYear=expectedGraduationYear;
        this.college=college;
        this.field=field;
        this.availability=availability;
        this.educationalLevel = educationalLevel;
        this.address=address;
        this.phoneNumber=phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEducationalLevel() {
        return educationalLevel;
    }

    public void setEducationalLevel(String educationalLevel) {
        this.educationalLevel = educationalLevel;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public String getStudentOrGraduate() {
        return studentOrGraduate;
    }

    public void setStudentOrGraduate(String studentOrGraduate) {
        this.studentOrGraduate = studentOrGraduate;
    }

    public String getExpectedGraduationYear() {
        return expectedGraduationYear;
    }

    public void setExpectedGraduationYear(String expectedGraduationYear) {
        this.expectedGraduationYear = expectedGraduationYear;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
