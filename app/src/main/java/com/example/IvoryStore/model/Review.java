package com.example.IvoryStore.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Review {
    private String userReview;
    private String userEmail;

    public Review(String userReview, String userEmail) {
        this.userReview = userReview;
        this.userEmail = userEmail;
    }

    public Review() {
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

}
