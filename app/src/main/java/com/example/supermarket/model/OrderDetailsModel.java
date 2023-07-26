package com.example.supermarket.model;

import java.util.ArrayList;

public class OrderDetailsModel {


    String currentDate;
    String currentTime;
    String totalPrice;
    String status;
    ArrayList<AddToCartModel> cartList = new ArrayList<>();
    String productName;
    String quantity;
    String price;
    String Location;
    String uid;

    public OrderDetailsModel() {
    }


    public OrderDetailsModel(String uid, String Location, String currentDate, String currentTime, String totalPrice, String status, ArrayList<AddToCartModel> cartList) {
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.cartList = cartList;
        this.Location = Location;
        this.uid = uid;
    }

    public OrderDetailsModel(String productName, String quantity, String price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderDetailsModel(String status) {
        this.status = status;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<AddToCartModel> getCartList() {
        return cartList;
    }

    public void setCartList(ArrayList<AddToCartModel> cartList) {
        this.cartList = cartList;
    }
}
