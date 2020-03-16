package com.onlyvtc.common;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InfoWindowData implements Serializable {

    private String address;
    private String arrival_time;
    private String distance;
    private int arrival_time_val;

    public int getDistance_val() {
        return distance_val;
    }

    public void setDistance_val(int distance_val) {
        this.distance_val = distance_val;
    }

    private int distance_val;

    public int getArrival_time_val() {
        return arrival_time_val;
    }

    public void setArrival_time_val(int arrival_time_val) {
        this.arrival_time_val = arrival_time_val;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

}
