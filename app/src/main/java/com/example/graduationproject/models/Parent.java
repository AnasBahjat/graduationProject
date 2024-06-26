package com.example.graduationproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Parent implements Parcelable {
    private String email ;
    private String idNumber ;
    private String phoneNumber ;
    private List<Children> childrenList;
    private String city;
    private String country ;
    private String firstName ;
    private String lastName ;
    private String birthDate ;
    private int parentId ;
    private List<Address>  addressList ;
    private List<String> phoneNumbersList;

    public Parent(String email,String idNumber,String firstName,String lastName,String birthDate,int parentId,List<Address> addressList,List<String> phoneNumbersList){
        this.email=email;
        this.idNumber=idNumber;
        this.firstName=firstName;
        this.lastName=lastName;
        this.birthDate=birthDate;
        this.parentId=parentId;
        this.addressList=addressList;
        this.phoneNumbersList=phoneNumbersList;
    }

    public Parent(String email,String idNumber, String phoneNumber, List<Children> childrenList, String city, String country) {
        this.email=email;
        this.idNumber = idNumber;
        this.phoneNumber = phoneNumber;
        this.childrenList = childrenList;
        this.city = city;
        this.country = country;
    }

    protected Parent(Parcel in) {
        email = in.readString();
        idNumber = in.readString();
        phoneNumber = in.readString();
        childrenList = in.createTypedArrayList(Children.CREATOR);
        city = in.readString();
        country = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        birthDate = in.readString();
        parentId = in.readInt();
        addressList = in.createTypedArrayList(Address.CREATOR);
        phoneNumbersList = in.createStringArrayList();
    }

    public static final Creator<Parent> CREATOR = new Creator<Parent>() {
        @Override
        public Parent createFromParcel(Parcel in) {
            return new Parent(in);
        }

        @Override
        public Parent[] newArray(int size) {
            return new Parent[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public List<String> getPhoneNumbersList() {
        return phoneNumbersList;
    }

    public void setPhoneNumbersList(List<String> phoneNumbersList) {
        this.phoneNumbersList = phoneNumbersList;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(idNumber);
        dest.writeString(phoneNumber);
        dest.writeList(childrenList);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(birthDate);
        dest.writeInt(parentId);
        dest.writeList(addressList);
        dest.writeList(phoneNumbersList);
    }
}
