package com.example.fullbinz.Model;

public class TongSampah {
    private String by;
    private String id;
    private String lastcollected;
    private String lastupdated;
    private double latitude;
    private double longitude;
    private String place;
    private String status;

    public TongSampah() {
    }

    public TongSampah(String by, String id, String lastcollected, String lastupdated, double latitude, double longitude, String place, String status) {
        this.by = by;
        this.id = id;
        this.lastcollected = lastcollected;
        this.lastupdated = lastupdated;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place = place;
        this.status = status;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastcollected() {
        return lastcollected;
    }

    public void setLastcollected(String lastcollected) {
        this.lastcollected = lastcollected;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //    private String place;
//    private long time;
//    private String status;
//    private double lat;
//    private double lon;
//    private String detailLink;
//
//    public TongSampah(String place, long time, String status, double lat, double lon, String detailLink) {
//        this.place = place;
//        this.time = time;
//        this.status = status;
//        this.lat = lat;
//        this.lon = lon;
//        this.detailLink = detailLink;
//    }
//
//    public TongSampah() {
//    }
//
//    public String getPlace() {
//        return place;
//    }
//
//    public void setPlace(String place) {
//        this.place = place;
//    }
//
//    public long getTime() {
//        return time;
//    }
//
//    public void setTime(long time) {
//        this.time = time;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public double getLat() {
//        return lat;
//    }
//
//    public void setLat(double lat) {
//        this.lat = lat;
//    }
//
//    public double getLon() {
//        return lon;
//    }
//
//    public void setLon(double lon) {
//        this.lon = lon;
//    }
//
//    public String getDetailLink() {
//        return detailLink;
//    }
//
//    public void setDetailLink(String detailLink) {
//        this.detailLink = detailLink;
//    }

}
