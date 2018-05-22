package com.example.IvoryStore.analytics;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.appsee.Appsee;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.example.IvoryStore.R;
import com.example.IvoryStore.model.IvoryProduct;

import java.util.HashMap;
import java.util.Map;


public class AnalyticsManager {
    private static String TAG = "AnalyticsManager";
    private static AnalyticsManager mInstance = null;
    private FirebaseAnalytics mFirebaseAnalytics;

    private AnalyticsManager() {



    }

    public static AnalyticsManager getInstance() {

        if (mInstance == null) {
            mInstance = new AnalyticsManager();
        }
        return (mInstance);
    }

    public void init(Context context) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

//        Appsee.start();

    }

    public void trackSearchEvent(String searchString) {

        String eventName = "search";

        //Firebase
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchString);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH,params);


//        //AppSee
//        Map<String, Object> eventParams2 = new HashMap<String, Object>();
//        eventParams2.put("search term", searchString);
//        Appsee.addEvent(eventName,eventParams2);


    }

    public void trackSignupEvent(String signupMethod) {

        String eventName = "signup";
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, signupMethod);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP,params);

//        //AppSee
//        Map<String, Object> eventParams2 = new HashMap<String, Object>();
//        eventParams2.put("signup method", signupMethod);
//        Appsee.addEvent(eventName,eventParams2);

    }


    public void trackLoginEvent(String loginMethod) {

        String eventName = "login";
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, loginMethod);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN,params);

//        //AppSee
//        Map<String, Object> eventParams2 = new HashMap<String, Object>();
//        eventParams2.put("signup method", loginMethod);
//
//        Appsee.addEvent(eventName,eventParams2);

    }

    public void trackProductEvent(String event , IvoryProduct product) {
        Bundle params = new Bundle();

        params.putString("product_name", product.getName());
        params.putString("product_death_reason", product.getDeathReason());
        params.putString("product_origin_continent", product.getOriginContinent());
        params.putDouble("product_price",product.getPrice());
        params.putDouble("product_weight",product.getWeight());
        params.putDouble("product_elephant_age",product.getElephantAge());

        mFirebaseAnalytics.logEvent(event,params);


//        //AppSee
//        Map<String, Object> eventParams2 = new HashMap<String, Object>();
//        eventParams2.put("product_name", product.getName());
//        eventParams2.put("product_death_reason", product.getDeathReason());
//        eventParams2.put("product_origin_continent", product.getOriginContinent());
//        eventParams2.put("product_price",product.getPrice());
//        eventParams2.put("product_weight",product.getWeight());
//        eventParams2.put("product_elephant_age",product.getElephantAge());
//
//
//        Appsee.addEvent(event,eventParams2);

    }

    public void trackPurchase(IvoryProduct product) {

        String eventName = "purchase";
        Bundle params = new Bundle();
        params.putDouble(FirebaseAnalytics.Param.PRICE,product.getPrice());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE,params);


//        //AppSee
//        Map<String, Object> eventParams2 = new HashMap<String, Object>();
//        eventParams2.put("product_name", product.getName());
//        eventParams2.put("product_death_reason", product.getDeathReason());
//        eventParams2.put("product_origin_continent", product.getOriginContinent());
//        eventParams2.put("product_price",product.getPrice());
//        eventParams2.put("product_weight",product.getWeight());
//        eventParams2.put("product_elephant_age",product.getElephantAge());
//
//        Appsee.addEvent(eventName,eventParams2);

    }

    public void trackSongRating(IvoryProduct product ,int userRating) {

        String eventName = "song_rating";
        Bundle params = new Bundle();

        params.putString("product_name", product.getName());
        params.putString("product_death_reason", product.getDeathReason());
        params.putString("product_origin_continent", product.getOriginContinent());
        params.putDouble("product_price",product.getPrice());
        params.putDouble("product_weight",product.getWeight());
        params.putDouble("product_elephant_age",product.getElephantAge());

        mFirebaseAnalytics.logEvent(eventName,params);

//        //AppSee
//        Map<String, Object> eventParams2 = new HashMap<String, Object>();
//        eventParams2.put("product_name", product.getName());
//        eventParams2.put("product_death_reason", product.getDeathReason());
//        eventParams2.put("product_origin_continent", product.getOriginContinent());
//        eventParams2.put("product_price",product.getPrice());
//        eventParams2.put("product_weight",product.getWeight());
//        eventParams2.put("product_elephant_age",product.getElephantAge());
//
//        Appsee.addEvent(eventName,eventParams2);

    }

    public void setUserID(String id) {

        mFirebaseAnalytics.setUserId(id);

        Appsee.setUserId(id);
    }

    public void setUserProperty(String name , String value) {

        mFirebaseAnalytics.setUserProperty(name,value);
    }

}