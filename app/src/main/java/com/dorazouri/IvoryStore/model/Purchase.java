package com.dorazouri.IvoryStore.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Purchase implements Parcelable {
    private long purchaseTime;
    private String sku;
    private String orderId;
    private String purchaseToken;
    private int amount;


    public Purchase(long purchaseTime, String sku, String orderId, String purchaseToken, int amount) {
        this.purchaseTime = purchaseTime;
        this.sku = sku;
        this.orderId = orderId;
        this.purchaseToken = purchaseToken;
        this.amount = amount;
    }

    public Purchase() {
    }

    private Purchase(Parcel in) {
        this.purchaseTime = in.readLong();
        this.sku = in.readString();
        this.orderId = in.readString();
        this.purchaseToken = in.readString();
        this.amount = in.readInt();
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }
    public String getSku() {
        return sku;
    }
    public String getOrderId() {
        return orderId;
    }
    public String getPurchaseToken() {
        return purchaseToken;
    }
    public int getAmount() {
        return amount;
    }


    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("purchaseTime", purchaseTime);
        result.put("sku", sku);
        result.put("orderId", orderId);
        result.put("purchaseToken", purchaseToken);
        result.put("amount", amount);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(purchaseTime);
        parcel.writeString(sku);
        parcel.writeString(orderId);
        parcel.writeString(purchaseToken);
        parcel.writeInt(amount);
    }

    public static final Creator<Purchase> CREATOR = new Creator<Purchase>() {
        @Override
        public Purchase createFromParcel(Parcel source) {
            return new Purchase(source);
        }

        @Override
        public Purchase[] newArray(int size) {
            return new Purchase[size];
        }
    };
}
