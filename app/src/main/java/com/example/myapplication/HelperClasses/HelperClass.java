package com.example.myapplication.HelperClasses;

import java.io.Serializable;

public class HelperClass implements Serializable {
    String name;
    String email;
    String password;
    String phone;
    String url;
    String roomName,address,city,minBook,maxBook,Rent,status;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HelperClass(String roomName, String address, String city, String minBook, String maxBook, String rent) {
        this.roomName = roomName;
        this.address = address;
        this.city = city;
        this.minBook = minBook;
        this.maxBook = maxBook;
        Rent = rent;
    }

    public HelperClass() {
    }

    public HelperClass(String roomName, String address, String city, String minBook, String maxBook, String rent, String status) {
        this.roomName = roomName;
        this.address = address;
        this.city = city;
        this.minBook = minBook;
        this.maxBook = maxBook;
        this.Rent = rent;
        this.status = status;

    }

    public HelperClass(String roomName, String address, String city, String minBook, String maxBook, String rent,String status,String uri) {
        this.roomName = roomName;
        this.address = address;
        this.city = city;
        this.minBook = minBook;
        this.maxBook = maxBook;
        this.Rent = rent;
        this.status = status;
        this.url = uri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMinBook() {
        return minBook;
    }

    public void setMinBook(String minBook) {
        this.minBook = minBook;
    }

    public String getMaxBook() {
        return maxBook;
    }

    public void setMaxBook(String maxBook) {
        this.maxBook = maxBook;
    }

    public String getRent() {
        return Rent;
    }

    public void setRent(String rent) {
        this.Rent = rent;
    }

    public HelperClass(String name, String email, String password, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
