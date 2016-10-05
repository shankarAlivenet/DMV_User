package com.alivenet.dmvtaxi.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alivenet 05 on 7/12/2016.
 */
public class Driverdetails implements Serializable{
    private String userId;
    private String id;
    private String driverId;
    private String fullName;
    private String image;
    private String mobile;
    private String cabType;
    private String cabDetail;
    private String licenceInfo;
    private String rating;
    ArrayList<Driverdetailslocation> driverdetailslocations;

    public ArrayList<Driverdetailslocation> getDriverdetailslocations() {
        return driverdetailslocations;
    }

    public void setDriverdetailslocations(ArrayList<Driverdetailslocation> driverdetailslocations) {
        this.driverdetailslocations = driverdetailslocations;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCabType() {
        return cabType;
    }

    public void setCabType(String cabType) {
        this.cabType = cabType;
    }

    public String getCabDetail() {
        return cabDetail;
    }

    public void setCabDetail(String cabDetail) {
        this.cabDetail = cabDetail;
    }

    public String getLicenceInfo() {
        return licenceInfo;
    }

    public void setLicenceInfo(String licenceInfo) {
        this.licenceInfo = licenceInfo;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}