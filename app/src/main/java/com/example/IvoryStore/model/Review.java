package com.example.IvoryStore.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Review implements Parcelable {
    private String userReview;
    private String userEmail;

    public Review(String userReview, String userEmail) {
        this.userReview = userReview;
        this.userEmail = userEmail;
    }

    public Review() {
    }

    private Review(Parcel in) {
        this.userReview = in.readString();
        this.userEmail = in.readString();
    }

    public String getUserReview() {
        return userReview;
    }

    public String getUserEmail() {
        return userEmail;
    }


    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("userReview", userReview);
        result.put("userEmail", userEmail);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userReview);
        parcel.writeString(userEmail);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
