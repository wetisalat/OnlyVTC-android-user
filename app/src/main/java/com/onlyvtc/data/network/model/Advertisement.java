package com.onlyvtc.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Advertisement {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("click_url")
    @Expose
    private String click_url;
    @SerializedName("image")
    @Expose
    private String image;

    public int getId() {
        return id;
    }

    public String getClick_url() {
        return click_url;
    }

    public String getImage() {
        return image;
    }

}
