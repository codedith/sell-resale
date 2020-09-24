package com.waseet.waseetapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyAdsResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("ads")
    @Expose
    private List<Product> ads = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Product> getAds() {
        return ads;
    }

    public void setAds(List<Product> ads) {
        this.ads = ads;
    }
}
