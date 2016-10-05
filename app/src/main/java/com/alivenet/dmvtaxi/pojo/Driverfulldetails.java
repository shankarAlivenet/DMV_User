package com.alivenet.dmvtaxi.pojo;

import java.io.Serializable;

/**
 * Created by ranjeet on 7/16/2016.
 */
public class Driverfulldetails implements Serializable{

    public String drivername;
    public String driverId1;
    public String licenceplate;
    public String taximodel;
    public String imageUrl="";
    public String mobileNO;
    public String vehicle;
    public String message;


    public boolean flag;


    public String getDriverId1() {
        return driverId1;
    }

    public void setDriverId1(String driverId1) {
        this.driverId1 = driverId1;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getMobileNO() {
        return mobileNO;
    }

    public void setMobileNO(String mobileNO) {
        this.mobileNO = mobileNO;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getLicenceplate() {
        return licenceplate;
    }

    public void setLicenceplate(String licenceplate) {
        this.licenceplate = licenceplate;
    }

    public String getTaximodel() {
        return taximodel;
    }

    public void setTaximodel(String taximodel) {
        this.taximodel = taximodel;
    }
}
