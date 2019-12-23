package com.android.busroute;

public class ListModel {

    private String starTime;

    public String getStarTime() {
        return starTime;
    }

    public void setStarTime(String starTime) {
        this.starTime = starTime;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(String totalSeats) {
        this.totalSeats = totalSeats;
    }

    private String available;
    private String totalSeats;

    public ListModel(String starTime, String available, String totalSeats) {
        this.starTime = starTime;
        this.available = available;
        this.totalSeats = totalSeats;
    }

}
