package com.spacex.builder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonBuilder {

    private final Gson gson;

    public JsonBuilder(Gson gson) {
        this.gson = gson;
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public String buildSuccessFilter(boolean success) {
        JsonObject query = new JsonObject();
        query.addProperty("success", success);

        JsonObject options = new JsonObject();
        options.addProperty("limit", 200);

        JsonObject body = new JsonObject();
        body.add("query", query);
        body.add("options", options);

        return gson.toJson(body);
    }

    public String buildDateFilter(String dateFrom, String dateTo) {
        JsonObject dateRange = new JsonObject();
        dateRange.addProperty("$gte", dateFrom + "T00:00:00.000Z");
        dateRange.addProperty("$lte", dateTo + "T23:59:59.000Z");

        JsonObject query = new JsonObject();
        query.add("date_utc", dateRange);

        JsonObject options = new JsonObject();
        options.addProperty("limit", 200);

        JsonObject body = new JsonObject();
        body.add("query", query);
        body.add("options", options);

        return gson.toJson(body);
    }
}
