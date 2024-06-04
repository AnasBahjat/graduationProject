package com.example.graduationproject.models;

public class Teacher {
    private String email;
    private String idNumber;
    private String studentOrGraduate;
    private String expectedGraduationYear;
    private String college;
    private String field;
    private String daysAvailableWeekly;
    private String hoursAvailableDaily;
    private Address address;
    private Profile teacherProfile;
    public Teacher(String email,String idNumber,
                   String studentOrGraduate,String expectedGraduationYear,
                   String college,String field,
                   String daysAvailableWeekly,
                   String hoursAvailableDaily,Address address){

        this.email=email;
        this.idNumber=idNumber;
        this.studentOrGraduate=studentOrGraduate;
        this.expectedGraduationYear=expectedGraduationYear;
        this.college=college;
        this.field=field;
        this.daysAvailableWeekly=daysAvailableWeekly;
        this.hoursAvailableDaily = hoursAvailableDaily;
        this.address=address;
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

    public String getDaysAvailableWeekly() {
        return daysAvailableWeekly;
    }

    public void setDaysAvailableWeekly(String daysAvailableWeekly) {
        this.daysAvailableWeekly = daysAvailableWeekly;
    }

    public String getHoursAvailableDaily() {
        return hoursAvailableDaily;
    }

    public void setHoursAvailableDaily(String hoursAvailableDaily) {
        this.hoursAvailableDaily = hoursAvailableDaily;
    }

    public Profile getTeacherProfile() {
        return teacherProfile;
    }

    public void setTeacherProfile(Profile teacherProfile) {
        this.teacherProfile = teacherProfile;
    }
}
