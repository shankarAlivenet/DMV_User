package com.alivenet.dmvtaxi.pojo;

import java.io.Serializable;

/**
 * Created by Alivenet 05 on 7/12/2016.
 */
public class Driverdetailslocation implements Serializable{

    private String latitude;
    private String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
