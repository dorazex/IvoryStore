package com.example.IvoryStore.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class IvoryProduct implements Parcelable {

    private String name;
    private String deathReason;
    private String origin;
    private int elephantAge;
    private String image;

    private int weight;
    private int price;
    private int reviewsCount;

    public IvoryProduct(String name, String deathReason, String origin, int elephantAge, String image, int weight, int price, int reviewsCount) {
        this.name = name;
        this.deathReason = deathReason;
        this.origin = origin;
        this.elephantAge = elephantAge;
        this.image = image;
        this.weight = weight;
        this.price = price;
        this.reviewsCount = reviewsCount;
    }

    public IvoryProduct() {
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public String getDeathReason() {
        return deathReason;
    }
    public void setDeathReason(String deathReason) {
        this.deathReason = deathReason;
    }

    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String genere) {
        this.origin = origin;
    }

    public String getElephantAge() {
        return Integer.toString(elephantAge);
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
        this.weight= weight;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public int getReviewsCount() { return reviewsCount; }
    public void incrementReviewCount() { reviewsCount++;}
    public void decrementReviewCount() { reviewsCount--;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(deathReason);
        parcel.writeString(origin);
        parcel.writeInt(elephantAge);
        parcel.writeString(image);
        parcel.writeInt(weight);
        parcel.writeInt(price);
        parcel.writeInt(reviewsCount);
    }

    private IvoryProduct(Parcel in){
        this.name = in.readString();
        this.deathReason = in.readString();
        this.origin = in.readString();
        this.elephantAge = in.readInt();
        this.image = in.readString();
        this.weight = in.readInt();
        this.price = in.readInt();
        this.reviewsCount = in.readInt();
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
