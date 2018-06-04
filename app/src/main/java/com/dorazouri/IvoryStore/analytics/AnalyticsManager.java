package com.dorazouri.IvoryStore.analytics;

import android.content.Context;
import android.os.Bundle;

import com.appsee.Appsee;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.dorazouri.IvoryStore.model.IvoryProduct;

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

        Appsee.start();

    }

    public void trackSearchEvent(String searchString) {

        String eventName = FirebaseAnalytics.Event.SEARCH;

        //Firebase
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchString);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH,params);


        //AppSee
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put(FirebaseAnalytics.Param.SEARCH_TERM, searchString);
        Appsee.addEvent(eventName,eventParams);
    }

    public void trackSignupEvent(String signupMethod) {

        String eventName = "signup";
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, signupMethod);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP,params);

        //AppSee
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("signup_method", signupMethod);
        Appsee.addEvent(eventName,eventParams);

    }


    public void trackLoginEvent(String loginMethod) {

        String eventName = FirebaseAnalytics.Event.LOGIN;
        Bundle params = new Bundle();
        params.putString("login_method", loginMethod);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN,params);

        //AppSee
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("login_method", loginMethod);

        Appsee.addEvent(eventName,eventParams);

    }

    public void trackProductUseEvent(IvoryProduct product) {
        String eventName = "use";

        Bundle params = new Bundle();

        params.putString("product_name", product.getName());
        params.putString("product_death_reason", product.getDeathReason());
        params.putString("product_origin_continent", product.getOriginContinent());
        params.putDouble("product_price",product.getPrice());
        params.putDouble("product_weight",product.getWeight());
        params.putDouble("product_elephant_age",product.getElephantAge());

        mFirebaseAnalytics.logEvent(eventName,params);


        //AppSee
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("product_name", product.getName());
        eventParams.put("product_death_reason", product.getDeathReason());
        eventParams.put("product_origin_continent", product.getOriginContinent());
        eventParams.put("product_price",product.getPrice());
        eventParams.put("product_weight",product.getWeight());
        eventParams.put("product_elephant_age",product.getElephantAge());


        Appsee.addEvent(eventName,eventParams);

    }

    public void trackProductBuyEvent(IvoryProduct product) {
        String eventName = "buy";

        Bundle params = new Bundle();

        params.putString("product_name", product.getName());
        params.putString("product_death_reason", product.getDeathReason());
        params.putString("product_origin_continent", product.getOriginContinent());
        params.putDouble("product_price",product.getPrice());
        params.putDouble("product_weight",product.getWeight());
        params.putDouble("product_elephant_age",product.getElephantAge());

        mFirebaseAnalytics.logEvent(eventName,params);


        //AppSee
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("product_name", product.getName());
        eventParams.put("product_death_reason", product.getDeathReason());
        eventParams.put("product_origin_continent", product.getOriginContinent());
        eventParams.put("product_price",product.getPrice());
        eventParams.put("product_weight",product.getWeight());
        eventParams.put("product_elephant_age",product.getElephantAge());


        Appsee.addEvent(eventName,eventParams);

    }

    public void trackAnonymousTryBuyEvent(IvoryProduct product) {
        String eventName = "anonymous_try_buy";

        Bundle params = new Bundle();
        params.putString("product_name", product.getName());
        mFirebaseAnalytics.logEvent(eventName, params);


        //AppSee
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("product_name", product.getName());

        Appsee.addEvent(eventName,eventParams);

    }



    public void trackProductReview(IvoryProduct product , String review) {

        String eventName = "product_review";
        Bundle params = new Bundle();

        params.putString("product_name", product.getName());
        params.putString("product_death_reason", product.getDeathReason());
        params.putString("product_origin_continent", product.getOriginContinent());
        params.putDouble("product_price",product.getPrice());
        params.putDouble("product_weight",product.getWeight());
        params.putDouble("product_elephant_age",product.getElephantAge());
        params.putString("product_review", review);

        mFirebaseAnalytics.logEvent(eventName,params);

        //AppSee
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("product_name", product.getName());
        eventParams.put("product_death_reason", product.getDeathReason());
        eventParams.put("product_origin_continent", product.getOriginContinent());
        eventParams.put("product_price",product.getPrice());
        eventParams.put("product_weight",product.getWeight());
        eventParams.put("product_elephant_age",product.getElephantAge());

        Appsee.addEvent(eventName,eventParams);

    }

    public void setUserID(String id) {

        mFirebaseAnalytics.setUserId(id);

        Appsee.setUserId(id);
    }

    public void setUserProperty(String name , String value) {

        mFirebaseAnalytics.setUserProperty(name,value);
    }

}