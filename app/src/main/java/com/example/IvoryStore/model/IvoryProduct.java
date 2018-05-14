package com.example.IvoryStore.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class IvoryProduct implements Parcelable {

    private String name;
    private String deathReason;
    private String originContinent;
    private int elephantAge;
    private int weight;
    private int price;
    private String image;
    private HashMap<String,Review> reviews;

    public IvoryProduct(String name, String deathReason, String originContinent, int elephantAge, String image, int weight, int price, HashMap<String,Review> reviews) {
        this.name = name;
        this.deathReason = deathReason;
        this.originContinent = originContinent;
        this.elephantAge = elephantAge;
        this.weight = weight;
        this.price = price;
        this.image = image;
        this.reviews = reviews;
    }

    public IvoryProduct() {
    }

    private IvoryProduct(Parcel in) {
        this.name = in.readString();
        this.deathReason = in.readString();
        this.originContinent = in.readString();
        this.elephantAge = in.readInt();
        this.weight = in.readInt();
        this.price = in.readInt();
        this.image = in.readString();
        in.readHashMap(Review.class.getClassLoader());
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDeathReason() {
        return deathReason;
    }
    public void setDeathReason(String deathReason) {
        this.deathReason = deathReason;
    }

    public String getOriginContinent() {
        return originContinent;
    }
    public void setOriginContinent(String originContinent) {
        this.originContinent = originContinent;
    }

    public int getElephantAge() {
        return elephantAge;
    }
    public void setElephantAge(int elephantAge) {
        this.elephantAge = elephantAge;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public HashMap<String,Review> getReviews(){
        return this.reviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(deathReason);
        parcel.writeString(originContinent);
        parcel.writeInt(elephantAge);
        parcel.writeInt(weight);
        parcel.writeInt(price);
        parcel.writeString(image);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IvoryProduct)) {
            return false;
        }
        IvoryProduct prod = (IvoryProduct) o;
        return this.name == prod.getName() &&
                this.elephantAge == prod.getElephantAge() &&
                this.getDeathReason() == prod.getDeathReason() &&
                this.getOriginContinent() == prod.getOriginContinent() &&
                this.getPrice() == prod.getPrice() &&
                this.getWeight() == prod.getWeight();
    }

    public static final Parcelable.Creator<IvoryProduct> CREATOR = new Parcelable.Creator<IvoryProduct>() {
        @Override
        public IvoryProduct createFromParcel(Parcel source) {
            return new IvoryProduct(source);
        }

        @Override
        public IvoryProduct[] newArray(int size) {
            return new IvoryProduct[size];
        }
    };

}
