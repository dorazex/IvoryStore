package com.example.IvoryStore.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    private String email;
    private int totalPurchase;
    private List<String> products = new ArrayList<>();

    public User() {
    }

    public User(String email, int totalPurchase, List<String> products) {
        this.email = email;
        this.totalPurchase = totalPurchase;
        this.products = products;
    }

    public String getEmail() {
        return email;
    }
    public int getTotalPurchase() { return totalPurchase; }
    public List<String> getProducts() { return products; }

    public void updateTotalPurchase(int newPurcahsePrice) {
        this.totalPurchase += newPurcahsePrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeList(products);
    }

    public User(Parcel in) {
        this.email = in.readString();
        in.readList(products,String.class.getClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
