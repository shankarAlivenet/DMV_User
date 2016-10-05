package com.alivenet.dmvtaxi.pojo;

import java.io.Serializable;

/**
 * Created by ranjeet on 7/18/2016.
 */
public class RideDriverComplete implements Serializable{

    public String messageride;
    public String driverNameride;
    public String pickLatride;
    public String pickupaddress;
    public String destinationaddress;
    public String pickLongride;
    public String destLatride;
    public String destLongride;
    public String rideId;
    public String vehicle;
    public String driverIdride;
    public String licenseId;
    public String totalfare;

    public String getPickupaddress() {
        return pickupaddress;
    }

    public void setPickupaddress(String pickupaddress) {
        this.pickupaddress = pickupaddress;
    }

    public String getDestinationaddress() {
        return destinationaddress;
    }

    public void setDestinationaddress(String destinationaddress) {
        this.destinationaddress = destinationaddress;
    }

    public String getTotalfare() {
        return totalfare;
    }

    public void setTotalfare(String totalfare) {
        this.totalfare = totalfare;
    }



    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getDriverIdride() {
        return driverIdride;
    }

    public void setDriverIdride(String driverIdride) {
        this.driverIdride = driverIdride;
    }

    public String getMessageride() {
        return messageride;
    }

    public void setMessageride(String messageride) {
        this.messageride = messageride;
    }

    public String getDriverNameride() {
        return driverNameride;
    }

    public void setDriverNameride(String driverNameride) {
        this.driverNameride = driverNameride;
    }

    public String getPickLatride() {
        return pickLatride;
    }

    public void setPickLatride(String pickLatride) {
        this.pickLatride = pickLatride;
    }

    public String getPickLongride() {
        return pickLongride;
    }

    public void setPickLongride(String pickLongride) {
        this.pickLongride = pickLongride;
    }

    public String getDestLatride() {
        return destLatride;
    }

    public void setDestLatride(String destLatride) {
        this.destLatride = destLatride;
    }

    public String getDestLongride() {
        return destLongride;
    }

    public void setDestLongride(String destLongride) {
        this.destLongride = destLongride;
    }
}
