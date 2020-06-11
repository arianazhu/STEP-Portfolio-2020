package com.google.sps.data;

/** This class represents a marker on the homepage map. */
public class Marker {

    private double lat;

    private double lng;

    private String content;

    public Marker(double lat, double lng, String content) {
        this.lat = lat;
        this.lng = lng;
        this.content = content;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getContent() {
        return content;
    }
}