package com.alivenet.dmvtaxi.pojo;

/**
 * Created by narendra on 8/5/2016.
 */
public class PreSetLocationResource {


    public String id;
    public String Latitude;
    public String Longitude;
    public String Google_address;
    public String Short_address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getGoogle_address() {
        return Google_address;
    }

    public void setGoogle_address(String google_address) {
        Google_address = google_address;
    }

    public String getShort_address() {
        return Short_address;
    }

    public void setShort_address(String short_address) {
        Short_address = short_address;
    }
}
