package com.example.fullbinz.Model;

public class TongSampah {
    private String place;
    private long time;
    private String status;
    private double lat;
    private double lon;
    private String detailLink;

    public TongSampah(String place, long time, String status, double lat, double lon, String detailLink) {
        this.place = place;
        this.time = time;
        this.status = status;
        this.lat = lat;
        this.lon = lon;
        this.detailLink = detailLink;
    }

    public TongSampah() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

}
