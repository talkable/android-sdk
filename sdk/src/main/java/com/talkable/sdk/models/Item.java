package com.talkable.sdk.models;

import com.google.gson.annotations.SerializedName;

public class Item {
    Double price;
    Integer quantity;
    @SerializedName("product_id") String productId;
    String title;
    String url;
    @SerializedName("image_url")
    String imageUrl;

    public Item() {
    }

    public Item(Double price, Integer quantity, String productId) {
        this.price = price;
        this.quantity = quantity;
        this.productId = productId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
