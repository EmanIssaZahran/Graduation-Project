package com.example.supermarket.model;

public class ProductModel {
    private String product;
    private String description;
    private String category;
    private String quantity;
    private String price;
    private String image;

    private String timestamp;

    public ProductModel() {
    }

    public ProductModel(String product, String description, String category, String quantity, String price, String image, String timestamp) {
        this.product = product;
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
        this.timestamp = timestamp;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}
