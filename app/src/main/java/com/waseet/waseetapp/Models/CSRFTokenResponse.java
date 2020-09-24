package com.waseet.waseetapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CSRFTokenResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private CSRFToken data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public CSRFToken getData() {
        return data;
    }

    public void setData(CSRFToken data) {
        this.data = data;
    }
}
