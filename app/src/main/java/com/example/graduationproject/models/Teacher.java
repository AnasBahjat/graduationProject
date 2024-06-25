package com.example.graduationproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Teacher implements Parcelable {
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
    private List<Address> addressesList ;
    private List<String> phoneNumbersList ;

    private String teacherName;
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

    public Teacher(String email,String idNumber,
                   String studentOrGraduate,String expectedGraduationYear,
                   String college,String field,
                   String availability,
                   String educationalLevel,List<Address> addressesList,List<String> phoneNumbersList,String teacherName){

        this.email=email;
        this.idNumber=idNumber;
        this.studentOrGraduate=studentOrGraduate;
        this.expectedGraduationYear=expectedGraduationYear;
        this.college=college;
        this.field=field;
        this.availability=availability;
        this.educationalLevel = educationalLevel;
        this.addressesList=addressesList;
        this.phoneNumbersList=phoneNumbersList;
        this.teacherName = teacherName;
    }


    protected Teacher(Parcel in) {
        email = in.readString();
        idNumber = in.readString();
        studentOrGraduate = in.readString();
        expectedGraduationYear = in.readString();
        college = in.readString();
        field = in.readString();
        availability = in.readString();
        educationalLevel = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        phoneNumber = in.readString();
        addressesList = in.createTypedArrayList(Address.CREATOR);
        phoneNumbersList = in.createStringArrayList();
        teacherName = in.readString();
    }

    public static final Creator<Teacher> CREATOR = new Creator<Teacher>() {
        @Override
        public Teacher createFromParcel(Parcel in) {
            return new Teacher(in);
        }

        @Override
        public Teacher[] newArray(int size) {
            return new Teacher[size];
        }
    };

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public List<Address> getAddressesList() {
        return addressesList;
    }

    public void setAddressesList(List<Address> addressesList) {
        this.addressesList = addressesList;
    }

    public List<String> getPhoneNumbersList() {
        return phoneNumbersList;
    }

    public void setPhoneNumbersList(List<String> phoneNumbersList) {
        this.phoneNumbersList = phoneNumbersList;
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

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(idNumber);
        dest.writeString(studentOrGraduate);
        dest.writeString(expectedGraduationYear);
        dest.writeString(college);
        dest.writeString(field);
        dest.writeString(availability);
        dest.writeString(educationalLevel);
        dest.writeParcelable(address,0);
        dest.writeString(phoneNumber);
        dest.writeList(addressesList);
        dest.writeList(phoneNumbersList);
    }

}
