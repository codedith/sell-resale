package com.waseet.waseetapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CSRFToken {

    @SerializedName("csrf_test_name")
    @Expose
    private String csrfTestName;

    public String getCsrfTestName() {
        return csrfTestName;
    }

    public void setCsrfTestName(String csrfTestName) {
        this.csrfTestName = csrfTestName;
    }
}
