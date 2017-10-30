package com.raveltrips.android.ravel.models;

/**
 * Created by Akash Anjanappa on 17-04-2017.
 */

public class CartRecyclerModel {

    private String type = "Trip";
    private String name;
    private Double price;
    private String id;
    private Double rating;

    public CartRecyclerModel(){}

    public CartRecyclerModel(Trip trip){
        this.type = "Trip";
        this.id = trip.getId();
        this.rating = trip.getRating();
        this.price = trip.getPrice();
        this.name = trip.getName();
    }

    public CartRecyclerModel(Pindrop pindrop){
        this.type = "Pindrop";
        this.id = pindrop.getId();
        this.rating = pindrop.getRating();
        this.price = pindrop.getPrice();
        this.name = pindrop.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
