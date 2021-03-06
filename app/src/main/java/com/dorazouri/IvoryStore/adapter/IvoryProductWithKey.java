package com.dorazouri.IvoryStore.adapter;

import com.dorazouri.IvoryStore.model.IvoryProduct;


public class IvoryProductWithKey {
    private String key;
    private IvoryProduct ivoryProduct;

    public IvoryProductWithKey(String key, IvoryProduct ivoryProduct) {
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
