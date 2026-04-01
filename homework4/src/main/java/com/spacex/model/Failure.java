package com.spacex.model;

public class Failure {
    private int time;
    private Integer altitude;
    private String reason;

    public int getTime() {
        return time;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "время: " + time + "с" +
                (altitude != null ? ", высота: " + altitude + "км" : "") +
                (reason != null ? ", причина: " + reason : "");
    }
}
