package com.waseet.waseetapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdsDetails {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("ads")
    @Expose
    private Detail ads;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Detail getAds() {
        return ads;
    }

    public void setAds(Detail ads) {
        this.ads = ads;
    }
}
