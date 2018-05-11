package com.example.IvoryStore.adapter;

import com.example.IvoryStore.model.IvoryProduct;


public class SongWithKey {
    private String key;
    private IvoryProduct ivoryProduct;

    public SongWithKey(String key, IvoryProduct ivoryProduct) {
        this.key = key;
        this.ivoryProduct = ivoryProduct;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public IvoryProduct getIvoryProduct() {
        return ivoryProduct;
    }

    public void setIvoryProduct(IvoryProduct ivoryProduct) {
        this.ivoryProduct = ivoryProduct;
    }
}
