package com.alivenet.dmvtaxi.pojo;

/**
 * Created by navin on 8/1/2016.
 */
public class ReservationListModel {
    public String bookingId;
    public String bookingTime;
    public String Userid;
    public String Piclat;
    public String picAddress;
    public String destAddress;
    public String Piclong;
    public String destlat;
    public String destlong;
    public String Cabtype;
    public String revdate;
    public String status;
    public String created;
    public String update;

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public String getRevtime() {
        return revtime;
    }

    public void setRevtime(String revtime) {
        this.revtime = revtime;
    }

    public String revtime;


    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getPiclong() {
        return Piclong;
    }

    public void setPiclong(String piclong) {
        Piclong = piclong;
    }

    public String getPiclat() {
        return Piclat;
    }

    public void setPiclat(String piclat) {
        Piclat = piclat;
    }

    public String getDestlat() {
        return destlat;
    }

    public void setDestlat(String destlat) {
        this.destlat = destlat;
    }

    public String getDestlong() {
        return destlong;
    }

    public void setDestlong(String destlong) {
        this.destlong = destlong;
    }

    public String getCabtype() {
        return Cabtype;
    }

    public void setCabtype(String cabtype) {
        Cabtype = cabtype;
    }

    public String getRevdate() {
        return revdate;
    }

    public void setRevdate(String revdate) {
        this.revdate = revdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }
}
