package com.alivenet.dmvtaxi.pojo;

/**
 * Created by bali on 17/7/16.
 */
public class DriverTime {

    public String destination_addresses;
    public String origin_addresses;
    public String distance;
    public String distacevalue;
    public String duration;
    public String durationvalue;
    public String duration_in_traffic;
    public String duration_in_trafficvalue;

    public String getDuration_in_traffic() {
        return duration_in_traffic;
    }

    public void setDuration_in_traffic(String duration_in_traffic) {
        this.duration_in_traffic = duration_in_traffic;
    }

    public String getDuration_in_trafficvalue() {
        return duration_in_trafficvalue;
    }

    public void setDuration_in_trafficvalue(String duration_in_trafficvalue) {
        this.duration_in_trafficvalue = duration_in_trafficvalue;
    }

    public String getDestination_addresses() {
        return destination_addresses;
    }

    public void setDestination_addresses(String destination_addresses) {
        this.destination_addresses = destination_addresses;
    }

    public String getOrigin_addresses() {
        return origin_addresses;
    }

    public void setOrigin_addresses(String origin_addresses) {
        this.origin_addresses = origin_addresses;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistacevalue() {
        return distacevalue;
    }

    public void setDistacevalue(String distacevalue) {
        this.distacevalue = distacevalue;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDurationvalue() {
        return durationvalue;
    }

    public void setDurationvalue(String durationvalue) {
        this.durationvalue = durationvalue;
    }
}
