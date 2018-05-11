package com.example.IvoryStore.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    private String email;
    private int totalPurchase;
    private List<String> mySongs = new ArrayList<>();

    public User() {
    }

    public User(String email, int totalPurchase, List<String> mySongs) {
        this.email = email;
        this.totalPurchase = totalPurchase;
        this.mySongs = mySongs;
    }

    public String getEmail() {
        return email;
    }


    public void upgdateTotalPurchase(int newPurcahsePrice) {
        this.totalPurchase += newPurcahsePrice;
    }

    public List<String> getUserProducts() {
        return mySongs;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeList(mySongs);
    }

    public User(Parcel in) {
        this.email = in.readString();
        in.readList(mySongs,String.class.getClassLoader());
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
