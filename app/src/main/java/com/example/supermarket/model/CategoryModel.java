package com.example.supermarket.model;

import java.io.Serializable;

public class CategoryModel  implements Serializable {

    public String[] options;

    String category ;
    String image ;
    String timestamp;
    String uid;

    public CategoryModel() {
    }

    public CategoryModel(String category, String image,String timestamp) {
        this.image = image;
        this.timestamp  = timestamp;
        this.category = category;
    }

    public CategoryModel(String category, String image) {
        this.category = category;
        this.image = image;

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
