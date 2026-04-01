package com.spacex.model;

import com.google.gson.annotations.SerializedName;

public class Core {
    @SerializedName("core")
    private String coreId;

    private Integer flight;
    private Boolean reused;

    @SerializedName("landing_success")
    private Boolean landingSuccess;

    @SerializedName("landing_type")
    private String landingType;

    public String getCoreId() {
        return coreId;
    }

    public Integer getFlight() {
        return flight;
    }

    public Boolean getReused() {
        return reused;
    }

    public Boolean getLandingSuccess() {
        return landingSuccess;
    }

    public String getLandingType() {
        return landingType;
    }

    @Override
    public String toString() {
        return "id: " + coreId +
                (flight != null ? ", полёт #" + flight : "") +
                (reused != null ? ", повторное использование: " + (reused ? "да" : "нет") : "") +
                (landingSuccess != null ? ", посадка: " + (landingSuccess ? "успех" : "провал") : "");
    }
}
