package com.example.supermarket.model;

public class ProfileModel {

    private String uid;
    private String fullName;
    private String phone;
    private String address;
    private String email;
    private String image;
    private String accountType;
    private String timestamp;
    private String online;
    private String shopName;
    private String deliveryFee;

    public ProfileModel(){

    }

    public ProfileModel(String uid, String fullName, String phone, String address, String email, String image, String accountType, String timestamp, String online, String shopName, String deliveryFee) {
        this.uid = uid;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.image = image;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.online = online;
        this.shopName = shopName;
        this.deliveryFee = deliveryFee;
    }

    public ProfileModel(String uid, String fullName, String phone, String address, String email, String accountType, String timestamp, String online, String shopName, String deliveryFee) {
        this.uid = uid;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.online = online;
        this.shopName = shopName;
        this.deliveryFee = deliveryFee;
    }

    public ProfileModel(String uid, String fullName, String phone, String address, String email, String image, String accountType, String timestamp, String online) {
        this.uid = uid;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.image = image;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.online = online;
    }

    public ProfileModel(String uid, String fullName, String phone, String address, String email, String accountType, String timestamp, String online) {
        this.uid = uid;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.online = online;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
}
